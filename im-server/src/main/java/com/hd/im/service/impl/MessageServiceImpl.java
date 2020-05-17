package com.hd.im.service.impl;

import com.hd.im.service.MessageService;
import com.im.core.constants.ErrorCode;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.MessageBundle;
import com.im.core.entity.PublishMessage;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.GSONParser;
import com.im.core.utils.TimeUtils;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: MessageServiceImpl
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/12 16:42
 * @Version: 1.0.0
 **/
@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public boolean verifySendPermissions(HDIMProtocol.Message message) {
        return false;
    }

    @Override
    public void storeMessage(MessageBundle messageBundle) {
        String key = String.format(RedisConstants.MESSAGE_STORE, messageBundle.getMessageId());
        RedisStandalone.REDIS.setExpire(key, GSONParser.getInstance().toJson(messageBundle), 60 * 60 * 24 * 7);
        /* 异步存储队列 */

    }

    @Override
    public int sendMessage(PublishMessage publishMessage) {
        /* 获取发送双发的用户ID */
        HDIMProtocol.Message        message        = publishMessage.toMessage();
        String                      clientId       = publishMessage.getClientId();
        String                      userId         = publishMessage.getUserId();
        HDIMProtocol.MessageContent messageContent = message.getMessageContent();
        /* 设置推送内容 */
        String pushContent = getPushContent(message.getMessageContent());
        /* 获取用户列表 */
        List<String> receives = getReceives(message);

        post(userId, clientId, receives, pushContent, message);

        return ErrorCode.SUCCESS;
    }

    public void post(String fromUser, String fromClientId, List<String> receives, String pushContent, HDIMProtocol.Message message) {
        if (receives != null && receives.size() > 0) {

            receives.forEach((receive) -> {
                /* 保存消息到用户收件箱中 */
                String messageInbox = String.format(RedisConstants.USER_MESSAGE_INBOX, receive);
                Long   messageSeq   = TimeUtils.getMicTime();
                RedisStandalone.REDIS.zadd(messageInbox, messageSeq.doubleValue(), String.valueOf(message.getMessageId()));

                /* 获取用户客户端信息 */
                String      clientsKey = String.format(RedisConstants.USER_CLIENTS, fromUser);
                Set<String> clients    = RedisStandalone.REDIS.smembers(clientsKey);
                if (clients == null || clients.size() == 0) {
                    return;
                }
                clients.forEach((client) -> {
                    /* 接收端的clientID与发送端一致,不需要推送 */
                    if (client.equals(fromClientId)) {
                        return;
                    }
                    
                });

            });
        }
    }

    /**
     * @param message
     * @return
     * @description: 获取消息接收集合
     */
    private List<String> getReceives(HDIMProtocol.Message message) {
        HDIMProtocol.Conversation conversation = message.getConversation();
        List<String>              receives     = new ArrayList<>();
        if (HDIMProtocol.ConversationType.SIGNAL_VALUE == conversation.getType()) {
            receives.add(message.getFrom());
            receives.add(conversation.getTarget());
        } else if (HDIMProtocol.ConversationType.GROUP_VALUE == conversation.getType()) {
            /* 根据群ID获取群成员列表 */
        }
        return receives;
    }

    /**
     * @param messageContent
     * @return
     * @description: 消息内容补全
     */
    private String getPushContent(HDIMProtocol.MessageContent messageContent) {
        String                              pushContent = null;
        HDIMProtocol.MessageContent.Builder builder     = messageContent.toBuilder();
        int                                 contentType = messageContent.getContentType();
        if (HDIMProtocol.MessageContentType.TEXT_VALUE == contentType) {
            pushContent = new String(messageContent.getData().toByteArray(), CharsetUtil.UTF_8);
        } else if (HDIMProtocol.MessageContentType.IMAGE_VALUE == contentType) {
            pushContent = "[图片]";
        } else if (HDIMProtocol.MessageContentType.SOUND_VALUE == contentType) {
            pushContent = "[语音]";
        } else if (HDIMProtocol.MessageContentType.FILE_VALUE == contentType) {
            pushContent = "[文件]";
        } else if (HDIMProtocol.MessageContentType.LOCATION_VALUE == contentType) {
            pushContent = "[位置]";
        } else if (HDIMProtocol.MessageContentType.VIDEO_VALUE == contentType) {
            pushContent = "[视频]";
        }
        return pushContent;
    }
}
