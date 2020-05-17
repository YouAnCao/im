package com.hd.im.netty.handler;

import com.im.core.constants.ErrorResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName: IMServerProtocolErrorHandler
 * @Description: 协议异常处理handler
 * @Author: Lyon.Cao
 * @Date: 2020/5/11 15:50
 * @Version: 1.0.0
 **/
public class IMServerProtocolErrorHandler extends SimpleChannelInboundHandler<ErrorResult> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ErrorResult errorResult) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer(2);
        buffer.writeByte(errorResult.getOp());
        buffer.writeByte(errorResult.getErrorCode());
        ctx.writeAndFlush(buffer);
    }
}
