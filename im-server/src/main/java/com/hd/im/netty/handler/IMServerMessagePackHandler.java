package com.hd.im.netty.handler;

import com.google.protobuf.ByteString;
import com.hd.im.cache.MemorySessionStore;
import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.handler.HandlerContext;
import com.hd.im.handler.IMHandler;
import com.hd.im.netty.channel.NettyChannelKeys;
import com.hd.im.service.UserService;
import com.im.core.constants.ErrorCode;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.AESHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: IMServerMessagePackHandler
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 10:41
 * @Version: 1.0.0
 **/
public class IMServerMessagePackHandler extends SimpleChannelInboundHandler<HDIMProtocol.MessagePack> {

    private Logger logger = LoggerFactory.getLogger(IMServerMessagePackHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HDIMProtocol.MessagePack messagePack) throws Exception {
        ByteBuf                transfer  = ctx.alloc().buffer();
        int                    errorCode = ErrorCode.SUCCESS;
        Attribute<UserSession> session   = ctx.channel().attr(NettyChannelKeys.USER_SESSION);
        HDIMProtocol.IMCommand command   = HDIMProtocol.IMCommand.forNumber(messagePack.getCommand());
        try {
            IMHandler handler = HandlerContext.getHandler(command);
            if (handler == null) {
                logger.error("command not support.");
                errorCode = ErrorCode.NO_RESPONSE;
                return;
            }
            if (HDIMProtocol.IMCommand.LOGIN == command) {
                errorCode = handler.execute(session, messagePack, transfer);
                if (errorCode == ErrorCode.SUCCESS) {
                    MemorySessionStore.getInstance().saveClient(session.get().getClientId(), ctx);
                }
                return;
            } else if (HDIMProtocol.IMCommand.LOGOUT != command && HDIMProtocol.IMCommand.PING != command) {
                /* 需要进行完全解密 */
                UserSession userSession = session.get();
                if (userSession == null) {
                    errorCode = ErrorCode.REQ_USER_NO_LOGIN;
                    return;
                }
                byte[] decrypt = null;
                try {
                    decrypt = AESHelper.decrypt(messagePack.getPayload().toByteArray(), session.get().getAesKey().getBytes());
                } catch (Exception e) {
                    logger.error("the publish decrypt fail.", e);
                    errorCode = ErrorCode.DECRYPT_DATA_FAIL;
                    return;
                }
                messagePack = messagePack.toBuilder().setPayload(ByteString.copyFrom(decrypt)).build();
            }
            errorCode = handler.execute(session, messagePack, transfer);

            /* 刷新最后存活时间 */
            UserSession userSession = ctx.channel().attr(NettyChannelKeys.USER_SESSION).get();
            if (userSession != null) {
                String clientId  = userSession.getClientId();
                String clientKey = String.format(RedisConstants.USER_SESSION, clientId);
                RedisStandalone.REDIS.hset(clientKey.getBytes(), "lastActiveTime".getBytes(), String.valueOf(System.currentTimeMillis()).getBytes());
            }

        } finally {
            HDIMProtocol.MessagePack.Builder builder = messagePack.toBuilder();
            if (ErrorCode.NO_RESPONSE != errorCode) {
                builder.setErrorCode(errorCode);
                if (transfer.readableBytes() > 0) {
                    byte[] data = new byte[transfer.readableBytes()];
                    transfer.readBytes(data);
                    builder.setPayload(ByteString.copyFrom(data));
                } else {
                    builder.setPayload(ByteString.EMPTY);
                }
                ctx.writeAndFlush(builder.build());
            }
            ReferenceCountUtil.release(transfer);
        }
    }
}
