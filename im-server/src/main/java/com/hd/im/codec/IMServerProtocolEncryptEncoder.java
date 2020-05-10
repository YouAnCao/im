package com.hd.im.codec;

import com.hd.im.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @Description: IM服务协议加密
 * @Author: Lyon.Cao
 * @Date: 2020/5/10 22:59
 * @Version: 1.0.0
 **/
public class IMServerProtocolEncryptEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        /* 只有推送响应需要加密 */
        int op = msg.readByte();
        if (HDIMProtocol.HeadType.PUBLISH.getNumber() == op) {
            
        }
        out.add(msg);
    }
}
