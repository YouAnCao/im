package com.hd.im.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.List;

/**
 * @Description: 响应编码
 * @Author: Lyon.Cao
 * @Date: 2020/5/10 23:14
 * @Version: 1.0.0
 **/
public class IMServerFrameEncoder extends LengthFieldPrepender {
    public IMServerFrameEncoder() {
        super(2);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
    }
}
