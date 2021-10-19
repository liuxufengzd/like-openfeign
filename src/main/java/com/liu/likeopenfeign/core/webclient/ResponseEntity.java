package com.liu.likeopenfeign.core.webclient;

import io.netty.handler.codec.http.HttpHeaders;

public class ResponseEntity<T> {
    private final T response;
    private final HttpHeaders httpHeaders;

    public T response() {
        return response;
    }

    public HttpHeaders headers() {
        return httpHeaders;
    }

    public ResponseEntity(T response, HttpHeaders httpHeaders) {
        this.response = response;
        this.httpHeaders = httpHeaders;
    }
}
