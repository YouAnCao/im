package com.hd.im.netty.codec;

import cn.hutool.core.codec.Base64;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hd.im.cache.MemorySessionStore;
import com.im.core.constants.ErrorResult;
import com.im.core.entity.UserSession;
import com.im.core.constants.ErrorCode;
import com.im.core.proto.HDIMProtocol;
import com.hd.im.netty.channel.NettyChannelKeys;
import com.im.core.utils.AESHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName: IMServerDecryptDecoder
 * @Description: 消息解码
 * @author: Lyon.Cao
 * @date 2020年5月10日 上午1:40:31
 */
public class IMServerProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    Logger logger = LoggerFactory.getLogger(IMServerProtocolDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > 0) {
            int    op  = byteBuf.readByte();
            byte[] dst = new byte[byteBuf.readableBytes()];
            if (dst.length > 0) {
                byteBuf.readBytes(dst);
            }
            logger.info("request operational :{}, remote info:{}, data length:{}", op, ctx.channel().remoteAddress(), dst.length);
            if (op == HDIMProtocol.HeadType.LOGIN.getNumber()) {
                /* 如果是登录的请求包，不需要全部解密 */
                HDIMProtocol.Login login = null;
                try {
                    login = HDIMProtocol.Login.parseFrom(dst);
                } catch (InvalidProtocolBufferException e) {
                    logger.error("the login data decode fail.", e);
                    ErrorResult errorResult = new ErrorResult(HDIMProtocol.HeadType.LOGIN_RESPONSE_VALUE, ErrorCode.REQ_LOGIN_DATA_DECODE_FAIL);
                    list.add(errorResult);
                    return;
                }
                list.add(login);
            } else if (HDIMProtocol.HeadType.PUBLISH.getNumber() == op) {
                /* 如果是推送的包，所有数据都需要进行解密 */
                Attribute<UserSession> session     = ctx.channel().attr(NettyChannelKeys.USER_SESSION);
                UserSession            userSession = null;
                if (session == null || (userSession = session.get()) == null) {
                    /* 没有登录 */
                    logger.error("the publish channel user session not exist.");
                    ErrorResult errorResult = new ErrorResult(HDIMProtocol.HeadType.PUBLISH_RESPONSE_VALUE, ErrorCode.REQ_USER_NO_LOGIN);
                    list.add(errorResult);
                    return;
                }
                HDIMProtocol.Publish publish = null;
                try {
                    publish = HDIMProtocol.Publish.parseFrom(dst);
                } catch (Exception e) {
                    logger.error("publish data decode fail.", e);
                    ErrorResult errorResult = new ErrorResult(HDIMProtocol.HeadType.PUBLISH_RESPONSE_VALUE, ErrorCode.REQ_DATA_PARSER_FAIL);
                    list.add(errorResult);
                    return;
                }
                /* 需要进行完全解密 */
                String decrypt = null;
                try {
                    decrypt = AESHelper.decrypt(publish.getPayload().toByteArray(), session.get().getAesKey());
                    if (decrypt == null || decrypt.length() == 0) {
                        throw new DecoderException();
                    }
                } catch (Exception e) {
                    logger.error("the publish decrypt fail.", e);
                    ErrorResult errorResult = new ErrorResult(HDIMProtocol.HeadType.PUBLISH_RESPONSE_VALUE, ErrorCode.DECRYPT_DATA_FAIL);
                    list.add(errorResult);
                    return;
                }
                HDIMProtocol.Publish newPublish = publish.toBuilder().setPayload(ByteString.copyFrom(decrypt.getBytes("utf-8"))).build();
                list.add(newPublish);
            } else if (HDIMProtocol.HeadType.LOGIN_OUT.getNumber() == op) {
                UserSession userSession = ctx.channel().attr(NettyChannelKeys.USER_SESSION).get();
                String      clientId    = userSession.getClientId();
                MemorySessionStore.getInstance().tickClient(clientId);
                ctx.close();
            }
        }
    }
}