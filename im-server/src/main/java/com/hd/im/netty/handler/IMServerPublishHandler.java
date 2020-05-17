package com.hd.im.netty.handler;

import com.google.protobuf.ByteString;
import com.hd.im.handler.HandlerContext;
import com.hd.im.handler.IMHandler;
import com.hd.im.netty.channel.NettyChannelKeys;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.utils.AESHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMServerPublishHandler extends SimpleChannelInboundHandler<HDIMProtocol.Publish> {

    private Logger logger = LoggerFactory.getLogger(IMServerPublishHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HDIMProtocol.Publish publish) throws Exception {
        long    sequenceId = publish.getSequenceId();
        int     errorCode  = ErrorCode.SUCCESS;
        ByteBuf payload    = ctx.alloc().buffer();
        payload.writeByte(HDIMProtocol.HeadType.PUBLISH_RESPONSE.getNumber());
        HDIMProtocol.PublishResponse publishResponse = HDIMProtocol.PublishResponse.newBuilder().setSequenceId(sequenceId).setPublishType(HDIMProtocol.PublishType.MS_VALUE).build();
        try {
            int                      publishTypeNumber = publish.getPublishType();
            HDIMProtocol.PublishType publishType       = null;
            try {
                publishType = HDIMProtocol.PublishType.forNumber(publishTypeNumber);
                if (publishType == null) {
                    throw new Exception();
                }
            } catch (Exception e) {
                logger.error("the publish type is no support.");
                errorCode = ErrorCode.UNKNOWN_PUBLISH_TYPE;
                return;
            }
            UserSession userSession = ctx.channel().attr(NettyChannelKeys.USER_SESSION).get();
            /* 找到具体执行类 */
            IMHandler handler  = HandlerContext.getHandler(publishType);
            ByteBuf   transfer = ctx.alloc().buffer();
            errorCode = handler.execute(publish, transfer);
            if (transfer.readableBytes() > 0) {
                byte[] data = new byte[transfer.readableBytes()];
                transfer.readBytes(data);
                String aesKey  = userSession.getAesKey();
                byte[] encrypt = AESHelper.encrypt(data, aesKey);
                publishResponse = publishResponse.toBuilder().setPayload(ByteString.copyFrom(encrypt)).build();
                payload.writeByte(ErrorCode.SUCCESS);
            }
        } finally {
            if (errorCode != ErrorCode.SUCCESS) {
                payload.writeByte(errorCode);
            }
            payload.writeBytes(publishResponse.toByteArray());
            ctx.writeAndFlush(payload);
        }
    }
}
