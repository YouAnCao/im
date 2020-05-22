package com.hd.im.netty.codec;

import cn.hutool.core.util.StrUtil;
import com.google.protobuf.ByteString;
import com.hd.im.netty.channel.NettyChannelKeys;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.utils.AESHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @ClassName: IMServerProtocolEncoder
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 11:28
 * @Version: 1.0.0
 **/
public class IMServerProtocolEncoder extends MessageToMessageEncoder<HDIMProtocol.MessagePack> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HDIMProtocol.MessagePack messagePack, List<Object> list) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        if (messagePack != null && messagePack.getErrorCode() == ErrorCode.SUCCESS && messagePack.getPayload() != null) {
            byte[] bytes = messagePack.getPayload().toByteArray();
            if (bytes != null && bytes.length > 0) {
                UserSession userSession = ctx.channel().attr(NettyChannelKeys.USER_SESSION).get();
                String      password    = null;
                if (userSession != null && StrUtil.isNotEmpty((password = userSession.getAesKey()))) {
                    byte[] encrypt = AESHelper.encrypt(bytes, password);
                    messagePack = messagePack.toBuilder().setPayload(ByteString.copyFrom(encrypt)).build();
                }
            }
        }
        buffer.writeBytes(messagePack.toByteArray());
        list.add(buffer);
    }
}
