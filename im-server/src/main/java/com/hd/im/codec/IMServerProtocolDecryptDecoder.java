package com.hd.im.codec;

import java.util.Base64;
import java.util.List;

import com.hd.im.channel.NettyChannelKeys;
import com.hd.im.entity.UserSession;
import com.hd.im.proto.HDIMProtocol;

import com.hd.im.utils.AESUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.Attribute;

/**
 * @ClassName: IMServerDecryptDecoder
 * @Description: 消息解密
 * @author: Lyon.Cao
 * @date 2020年5月10日 上午1:40:31
 */
public class IMServerProtocolDecryptDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list)
            throws Exception {
        if (byteBuf.readableBytes() > 0) {
            int    op  = byteBuf.readByte();
            byte[] dst = new byte[byteBuf.readableBytes()];
            if (dst.length > 0) {
                byteBuf.readBytes(dst);
            }

            if (op == HDIMProtocol.HeadType.LOGIN.getNumber()) {
                /* 如果是登录的请求包，不需要全部解密 */
                HDIMProtocol.Login login = HDIMProtocol.Login.parseFrom(dst);
                list.add(login);
            } else if (HDIMProtocol.HeadType.PUBLISH.getNumber() == op) {
                /* 如果是推送的包，所有数据都需要进行解密 */
                Attribute<UserSession> session = ctx.channel().attr(NettyChannelKeys.USER_SESSION);
                if (session == null) {
                    /* 没有登录 */
                    ctx.writeAndFlush(new byte[]{(byte) op, 5});
                    return;
                }
                /* 需要进行完全解密 */
                byte[]  decrypt  = AESUtils.decrypt(dst, session.get().getAesKey());
                ByteBuf transfer = ctx.alloc().buffer();
                transfer.writeByte(op);
                transfer.writeBytes(decrypt);
                list.add(transfer);
            } else {

            }
        }
    }
}
