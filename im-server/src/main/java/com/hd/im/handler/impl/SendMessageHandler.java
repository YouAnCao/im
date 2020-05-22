package com.hd.im.handler.impl;

import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.im.core.constants.ErrorCode;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.PublishMessage;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.GSONParser;
import com.im.core.utils.MessageShardingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: SendMessageHandler
 * @Description: 发送消息
 * @Author: Lyon.Cao
 * @Date: 2020/5/15 10:52
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.IMCommand.MS)
public class SendMessageHandler extends IMHandler {

    private Logger logger = LoggerFactory.getLogger(SendMessageHandler.class);

    @Override
    public int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        HDIMProtocol.Message message = null;
        try {
            message = HDIMProtocol.Message.parseFrom(messagePack.getPayload().toByteArray());
        } catch (Exception e) {
            logger.error("parser message fail.", e);
            return ErrorCode.REQ_DATA_PARSER_FAIL;
        }
        UserSession session   = userSession.get();
        long        messageId = MessageShardingUtil.generateId();
        long        timestamp = System.currentTimeMillis();
        /* 权限验证 */

        /* 异步分拣 */
        HDIMProtocol.Message rebuildMessage = message.toBuilder().setMessageId(messageId).build();
        PublishMessage       publishMessage = new PublishMessage(session.getUserId(), session.getClientId(), rebuildMessage);
        RedisStandalone.REDIS.lpush(RedisConstants.MESSAGE_SORTING, GSONParser.getInstance().toJson(publishMessage));

        /* 响应消息结果 */
        HDIMProtocol.MessageResponse messageResponse = HDIMProtocol.MessageResponse.newBuilder().setMessageId(messageId).setTimestamp(timestamp).build();
        payload.writeBytes(messageResponse.toByteArray());
        return ErrorCode.SUCCESS;
    }
}
