package com.liu.likeopenfeign.core.webclient;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

@ChannelHandler.Sharable
public class Receiver extends ChannelInboundHandlerAdapter {
    private FullHttpResponse fullHttpResponse;
    private ChannelHandlerContext context;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        context = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        fullHttpResponse = (FullHttpResponse) msg;
        notify();
    }

    public synchronized FullHttpResponse sendMessage(FullHttpRequest msg) {
        try {
            context.writeAndFlush(msg);
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fullHttpResponse;
    }
}
