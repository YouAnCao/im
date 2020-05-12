package com.hd.im.codec;

import cn.hutool.core.codec.Base64;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hd.im.cache.MemorySessionStore;
import com.hd.im.channel.NettyChannelKeys;
import com.hd.im.commons.constants.DecoderError;
import com.hd.im.commons.entity.UserSession;
import com.hd.im.commons.proto.HDIMProtocol;
import com.hd.im.commons.utils.AESUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName: IMServerDecryptDecoder
 * @Description: 消息解密
 * @author: Lyon.Cao
 * @date 2020年5月10日 上午1:40:31
 */
public class IMServerProtocolDecryptDecoder extends MessageToMessageDecoder<ByteBuf> {

    Logger logger = LoggerFactory.getLogger(IMServerProtocolDecryptDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > 0) {
            int    op  = byteBuf.readByte();
            byte[] dst = new byte[byteBuf.readableBytes()];
            if (dst.length > 0) {
                byteBuf.readBytes(dst);
            }

            if (op == HDIMProtocol.HeadType.LOGIN.getNumber()) {
                /* 如果是登录的请求包，不需要全部解密 */
                HDIMProtocol.Login login = null;
                try {
                    login = HDIMProtocol.Login.parseFrom(dst);
                } catch (InvalidProtocolBufferException e) {
                    logger.error("", e);
                    list.add(DecoderError.LOGIN);
                    return;
                }
                list.add(login);
            } else if (HDIMProtocol.HeadType.PUBLISH.getNumber() == op) {
                /* 如果是推送的包，所有数据都需要进行解密 */
                Attribute<UserSession> session = ctx.channel().attr(NettyChannelKeys.USER_SESSION);
                UserSession userSession = null;
                if (session == null || (userSession = session.get()) == null) {
                    /* 没有登录 */
                    return;
                }
                /* 需要进行完全解密 */
                byte[]  decrypt  = AESUtils.decrypt(dst, session.get().getAesKey());
                ByteBuf transfer = ctx.alloc().buffer();
                transfer.writeByte(op);
                transfer.writeBytes(decrypt);
                list.add(transfer);
            } else if (HDIMProtocol.HeadType.LOGIN_OUT.getNumber() == op){
                UserSession userSession = ctx.channel().attr(NettyChannelKeys.USER_SESSION).get();
                String clientId = userSession.getClientId();
                MemorySessionStore.getInstance().tickClient(clientId);
                ctx.close();
            }
        }
    }
}