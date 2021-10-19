package com.liu.likeopenfeign.core;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class RpcParam {
    private String hostname;
    private int port;
    private String uri;
    private Map<String, Object> pathParams;
    private HttpMethod method;
    private Object requestBody;
    private Class<?> responseClass;
}
