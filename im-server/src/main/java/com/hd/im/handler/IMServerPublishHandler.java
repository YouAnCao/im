package com.hd.im.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IMServerPublishHandler extends SimpleChannelInboundHandler<Integer> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Integer msg) throws Exception {
        System.out.println("触发int");
    }
}
