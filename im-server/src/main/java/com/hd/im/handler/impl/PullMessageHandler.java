package com.hd.im.handler.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.hd.im.service.MessageService;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: PullMessageHandler
 * @Description: 拉取消息
 * @Author: Lyon.Cao
 * @Date: 2020/5/15 10:53
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.PublishType.MP)
public class PullMessageHandler extends IMHandler {

    private Logger logger = LoggerFactory.getLogger(PullMessageHandler.class);

    @Override
    public int action(UserSession userSession, HDIMProtocol.Publish publish, ByteBuf payload) {
        HDIMProtocol.PullMessageRequest request = null;
        try {
            request = HDIMProtocol.PullMessageRequest.parseFrom(publish.getPayload().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            logger.error("parse the pull message request fail.", e);
            return ErrorCode.REQ_DATA_PARSER_FAIL;
        }
        long                             messageHead         = request.getMessageHead();
        String                           clientId            = userSession.getClientId();
        String                           userId              = userSession.getUserId();
        MessageService                   messageService      = ApplicationContextHolder.getApplicationContext().getBean("messageServiceImpl", MessageService.class);
        HDIMProtocol.PullMessageResponse pullMessageResponse = messageService.getMessage(clientId, userId, messageHead);
        logger.info("pull message from {}, request msg seq:{}, message size:{}", userId, messageHead, pullMessageResponse.getMessagesCount());
        payload.writeBytes(pullMessageResponse.toByteArray());
        return ErrorCode.SUCCESS;
    }
}