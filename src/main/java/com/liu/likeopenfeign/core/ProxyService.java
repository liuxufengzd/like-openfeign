package com.liu.likeopenfeign.core;

import com.liu.likeopenfeign.core.webclient.WebClient;
import io.netty.handler.codec.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;

public class ProxyService {
    private final RpcParam rpcParam = new RpcParam();

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                (proxy, method, args) -> {
                    FeignService feignService = clazz.getAnnotation(FeignService.class);
                    if (feignService == null)
                        throw new Exception("@FeignService should be annotated");
                    rpcParam.setHostname(feignService.hostname());
                    rpcParam.setPort(feignService.port());
                    initCall(method, args);
                    return sendRequest();
                });
    }

    private Object sendRequest() {
        WebClient webClient = WebClient.create(rpcParam.getHostname(), rpcParam.getPort());
        return webClient.uri(rpcParam.getUri(), rpcParam.getPathParams())
                .header()
                .method(rpcParam.getMethod())
                .bodyValue(rpcParam.getRequestBody())
                .retrieve()
                .toEntity(rpcParam.getResponseClass()).response();
    }

    private void initCall(Method method, Object[] args) throws Exception {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(RequestBody.class) != null)
                rpcParam.setRequestBody(args[i]);
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null)
                rpcParam.getPathParams().put(pathVariable.value(), args[i]);
        }
        initUriAndMethodType(method);
        rpcParam.setResponseClass(method.getReturnType());
    }

    private void initUriAndMethodType(Method method) {
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            rpcParam.setMethod(HttpMethod.GET);
            rpcParam.setUri(getMapping.value()[0]);
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            rpcParam.setMethod(HttpMethod.POST);
            rpcParam.setUri(postMapping.value()[0]);
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            rpcParam.setMethod(HttpMethod.DELETE);
            rpcParam.setUri(deleteMapping.value()[0]);
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            rpcParam.setMethod(HttpMethod.PUT);
            rpcParam.setUri(putMapping.value()[0]);
        }
    }
}
