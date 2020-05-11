package com.hd.im.handler;

import com.hd.im.commons.constants.DecoderError;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName: IMServerDecoderFailHandler
 * @Description: 数据转换异常
 * @Author: Lyon.Cao
 * @Date: 2020/5/11 15:50
 * @Version: 1.0.0
 **/
public class IMServerDecoderFailHandler extends SimpleChannelInboundHandler<DecoderError> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DecoderError decoderError) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer(2);
        buffer.writeByte(decoderError.getOperational());
        buffer.writeByte(decoderError.getErrorCode());
        ctx.writeAndFlush(buffer);
    }
}
