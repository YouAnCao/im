package com.hd.im.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @ClassName: IMServerDecoder
 * @Description: IM server 消息解码器
 * @author: Lyon.Cao
 * @date 2020年5月10日 上午1:39:11
 */
public class IMServerProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf != null && byteBuf.readableBytes() > 0) {
            /* 获取操作类型 */
            int op = byteBuf.readByte();
            /* 转换对应的操作数据类型 */
            int    len = byteBuf.readableBytes();
            byte[] dst = new byte[len];
            byteBuf.readBytes(dst);
            String string = new String(dst);
            list.add(string);
        }
    }
}
