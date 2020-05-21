package com.hd.im.netty.handler;

import com.im.core.constants.ErrorCode;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName: IMServerPingHandler
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/19 17:28
 * @Version: 1.0.0
 **/
public class IMServerPingHandler extends SimpleChannelInboundHandler<HDIMProtocol.Ping> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HDIMProtocol.Ping ping) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer(2);
        buffer.writeByte(HDIMProtocol.HeadType.PONG_VALUE);
        buffer.writeByte(ErrorCode.SUCCESS);
        ctx.writeAndFlush(buffer);
    }
}
