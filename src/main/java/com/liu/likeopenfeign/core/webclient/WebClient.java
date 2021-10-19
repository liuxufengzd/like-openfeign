package com.liu.likeopenfeign.core.webclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

// 仅仅处理了json和String类型的返回值
public class WebClient {
    private final String hostname;
    private final int port;
    private FullHttpResponse httpResponse;
    private RequestUriSpec requestUriSpec;
    private RequestHeaderSpec requestHeaderSpec;
    private RequestBodySpec requestBodySpec;
    private Receiver receiver;
    private NioEventLoopGroup group;
    private Channel channel;

    private WebClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public static WebClient create(String hostname, int port) {
        return new WebClient(hostname, port);
    }

    public RequestUriSpec uri() {
        return uri("/");
    }

    public RequestUriSpec uri(String uri) {
        requestUriSpec = new RequestUriSpec(uri);
        return requestUriSpec;
    }

    public RequestUriSpec uri(String uri, Map<String,?> pathVariables) {
        String[] strings = uri.split("/");
        String result = Arrays.stream(strings).map(string -> {
            if (string.startsWith("{") && string.endsWith("}"))
                return pathVariables.get(string.substring(1, string.length() - 1)).toString();
            return string;
        }).collect(Collectors.joining("/"));
        requestUriSpec = new RequestUriSpec(result);
        return requestUriSpec;
    }

    public class RequestUriSpec {
        private URI uri;

        RequestUriSpec(String uriString) {
            try {
                uri = new URI(uriString);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        public RequestHeaderSpec header() {
            return header(null);
        }

        public RequestHeaderSpec header(HttpHeaders httpHeaders) {
            requestHeaderSpec = new RequestHeaderSpec(httpHeaders);
            return requestHeaderSpec;
        }
    }

    public class RequestHeaderSpec {
        private final HttpHeaders httpHeaders;

        public RequestHeaderSpec(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
        }

        public RequestBodySpec post() {
            requestBodySpec = new RequestBodySpec(HttpMethod.POST);
            return requestBodySpec;
        }

        public RequestBodySpec get() {
            requestBodySpec = new RequestBodySpec(HttpMethod.GET);
            return requestBodySpec;
        }

        public RequestBodySpec put() {
            requestBodySpec = new RequestBodySpec(HttpMethod.PUT);
            return requestBodySpec;
        }

        public RequestBodySpec delete() {
            requestBodySpec = new RequestBodySpec(HttpMethod.DELETE);
            return requestBodySpec;
        }

        public RequestBodySpec method(HttpMethod method){
            requestBodySpec = new RequestBodySpec(method);
            return requestBodySpec;
        }
    }

    public class RequestBodySpec {
        private final HttpMethod httpMethod;
        private Object bodyValue = "";

        RequestBodySpec(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public RequestBodySpec bodyValue(Object bodyValue) {
            this.bodyValue = bodyValue;
            return this;
        }

        public ResponseSpec retrieve() {
            initClient();
            httpResponse = receiver.sendMessage(makeRequest(bodyValue));
            try {
                channel.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
            return new ResponseSpec();
        }
    }

    public class ResponseSpec {
        public <T> ResponseEntity<T> toEntity(Class<T> clazz) {
            String content = httpResponse.content().toString(CharsetUtil.UTF_8);
            return new ResponseEntity<>(StateUtil.readValue(content, clazz), httpResponse.headers());
        }
    }

    private void initClient() {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        receiver = new Receiver();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpClientCodec(),
                                new HttpObjectAggregator(512 * 1024), receiver);
                    }
                });
        try {
            channel = bootstrap.connect(hostname, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private FullHttpRequest makeRequest(Object bodyValue) {
        ByteBuf buf = Unpooled.copiedBuffer(StateUtil.writeValueAsBytes(bodyValue));
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                requestBodySpec.httpMethod, requestUriSpec.uri.toASCIIString(), buf);
        httpRequest.headers().set(HttpHeaderNames.HOST, hostname);
        httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        if (requestHeaderSpec.httpHeaders != null)
            requestHeaderSpec.httpHeaders.forEach(entry -> httpRequest.headers().set(entry.getKey(), entry.getValue()));
        return httpRequest;
    }
}
