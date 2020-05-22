package com.hd.im.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hd.im.api.entity.MessageInfo;
import com.hd.im.api.entity.UserMessageInfo;
import com.hd.im.api.service.MessageApi;
import com.hd.im.cache.MemorySessionStore;
import com.hd.im.service.MessageService;
import com.im.core.constants.ErrorCode;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.MessageBundle;
import com.im.core.entity.NotifyMessage;
import com.im.core.entity.PublishMessage;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.GSONParser;
import com.im.core.utils.TimeUtils;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName: MessageServiceImpl
 * @Description: 消息服务
 * @Author: Lyon.Cao
 * @Date: 2020/5/12 16:42
 * @Version: 1.0.0
 **/
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageApi messageApi;


    private Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    /**
     * @param user
     * @return
     * @description 获取消息头
     */
    @Override
    public long getMessageHead(String user) {
        String     messageInbox = String.format(RedisConstants.USER_MESSAGE_INBOX, user);
        Set<Tuple> tuples       = RedisStandalone.REDIS.zrangeWithScore(messageInbox, -1, -1);
        if (tuples == null || tuples.size() == 0) {
            return 0L;
        }
        Iterator<Tuple> iterator = tuples.iterator();
        long            msgHead  = 0l;
        while (iterator.hasNext()) {
            Tuple next = iterator.next();
            msgHead = new BigDecimal(next.getScore()).longValue();
        }
        return msgHead;
    }

    @Override
    public boolean verifySendPermissions(HDIMProtocol.Message message) {
        return false;
    }

    @Override
    public void storeMessage(String userId, String clientId, Long messageId, HDIMProtocol.Message message) {

        /* 存储消息到Redis */
        MessageBundle messageBundle = new MessageBundle(messageId, userId, clientId, message);
        String        messageStore  = String.format(RedisConstants.MESSAGE_STORE, messageId);
        RedisStandalone.REDIS.setExpire(messageStore, GSONParser.getInstance().toJson(messageBundle), 60 * 60 * 24 * 7);

        /* 异步存储队列 */
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setFrom(message.getFrom());
        messageInfo.setTarget(message.getConversation().getTarget());
        messageInfo.setMessageId(message.getMessageId());
        messageInfo.setType(message.getConversation().getType());
        messageInfo.setData(message.toByteArray());

        messageApi.storeMessage(messageInfo);
    }

    @Override
    public void storeUserMessage(String userId, Long messageId, Long messageSeq) {

        String messageInbox = String.format(RedisConstants.USER_MESSAGE_INBOX, userId);
        RedisStandalone.REDIS.zadd(messageInbox, messageSeq.doubleValue(), String.valueOf(messageId));

        UserMessageInfo userMessageInfo = new UserMessageInfo();
        userMessageInfo.setUserId(userId);
        userMessageInfo.setMessageId(messageId);
        userMessageInfo.setMessageSeq(messageSeq);
        messageApi.storeUserMessage(userMessageInfo);
    }

    @Override
    public int sendMessage(PublishMessage publishMessage) {
        /* 获取发送双发的用户ID */
        HDIMProtocol.Message message  = publishMessage.toMessage();
        String               clientId = publishMessage.getClientId();
        String               userId   = publishMessage.getUserId();
        //HDIMProtocol.MessageContent messageContent = message.getMessageContent();
        /* 设置推送内容 */
        String pushContent = getPushContent(message.getMessageContent());
        /* 获取用户列表 */
        List<String> receives = getReceives(message);
        storeMessage(userId, clientId, message.getMessageId(), message);

        post(userId, clientId, receives, pushContent, message);
        return ErrorCode.SUCCESS;
    }

    @Override
    public HDIMProtocol.PullMessageResponse getMessage(String clientId, String userId, Long requestHead) {
        /* 根据clientId与userId 获取服务器保存的用户最后拉取时间 */
        String                           messageHead     = String.format(RedisConstants.USER_MESSAGE_HEAD, userId, clientId);
        HDIMProtocol.PullMessageResponse messageResponse = null;
        if (requestHead > 0) {
            String userHead = RedisStandalone.REDIS.get(messageHead);
            if (requestHead > 0 && StrUtil.isNotEmpty(userHead) && userHead.matches("\\d+")) {
                long userHeadOnServer = Long.parseLong(userHead);
                if (userHeadOnServer > requestHead) {
                    messageResponse = HDIMProtocol.PullMessageResponse.newBuilder().setCurrent(userHeadOnServer).setHead(userHeadOnServer).build();
                    return messageResponse;
                }
            }
        }
        long current = requestHead;
        long head    = requestHead;

        /* 读取用户收件箱 */
        int                       dataLength   = 0;
        Set<HDIMProtocol.Message> messages     = new HashSet<>();
        String                    messageInbox = String.format(RedisConstants.USER_MESSAGE_INBOX, userId);
        Set<Tuple>                tuples       = RedisStandalone.REDIS.zrangeByScoreWithScores(messageInbox, String.valueOf(requestHead + 1), (System.currentTimeMillis() + 10000) + "000");
        if (tuples != null && tuples.size() > 0) {
            for (Tuple tuple : tuples) {
                /* 数据长度超过100KB时断开截断 */
                if (dataLength > 102400) {
                    break;
                }
                long messageSeq = new BigDecimal(tuple.getScore()).longValue();
                current = messageSeq;
                head = messageSeq;
                String messageId = tuple.getElement();
                try {
                    String messageStore   = String.format(RedisConstants.MESSAGE_STORE, messageId);
                    String messageContext = RedisStandalone.REDIS.get(messageStore);
                    if (StrUtil.isEmpty(messageContext)) {
                        continue;
                    }
                    MessageBundle messageBundle = GSONParser.getInstance().fromJson(messageContext, MessageBundle.class);
                    if (!clientId.equals(messageBundle.getFromClient()) || !userId.equals(messageBundle.getFromUser())) {
                        HDIMProtocol.Message message        = messageBundle.toMessage();
                        int                  serializedSize = message.getSerializedSize();
                        dataLength += serializedSize;
                        messages.add(message);
                    }
                } catch (Exception e) {
                    logger.error("parse message fail.", e);
                    continue;
                }
            }

            /* 消息出栈 */
            if (messages.size() > 0) {
                if (messages.size() < tuples.size()) {
                    Tuple last = (Tuple) tuples.toArray()[tuples.size() - 1];
                    head = new BigDecimal(last.getScore()).longValue();
                }
                messageResponse = HDIMProtocol.PullMessageResponse.newBuilder().setHead(head).setCurrent(current).
                        addAllMessages(messages).build();
            }
        } else {
            messageResponse = HDIMProtocol.PullMessageResponse.newBuilder().setCurrent(requestHead).setHead(requestHead).build();
        }
        RedisStandalone.REDIS.setExpire(messageHead, String.valueOf(current), 700);
        return messageResponse;
    }

    /**
     * @param fromUser
     * @param fromClientId
     * @param receives
     * @param pushContent
     * @param
     * @description 发送消息通知
     */
    private void post(String fromUser, String fromClientId, List<String> receives, String pushContent, HDIMProtocol.Message message) {
        if (receives != null && receives.size() > 0) {
            receives.forEach((receive) -> {
                /* 保存消息到用户收件箱中 */

                Long messageSeq = TimeUtils.getMicTime();

                storeUserMessage(receive, message.getMessageId(), messageSeq);

                List<UserSession> userSessions = MemorySessionStore.getInstance().getUserSessionsByUserName(receive);
                if (userSessions != null && userSessions.size() > 0) {
                    userSessions.forEach(userSession -> {
                        String clientId       = userSession.getClientId();
                        String userId         = userSession.getUserId();
                        Long   lastActiveTime = userSession.getLastActiveTime();
                        if (clientId.equals(fromClientId)) {
                            return;
                        }
                        if (lastActiveTime == null || System.currentTimeMillis() - lastActiveTime > 1000 * 60 * 60) {
                            logger.info("the last active time is out of the limiter.");
                            return;
                        }
                        HDIMProtocol.NotifyMessage notifyMessage  = HDIMProtocol.NotifyMessage.newBuilder().setHead(messageSeq).setTarget(receive).setType(message.getConversation().getType()).build();
                        NotifyMessage              notifyTransfer = new NotifyMessage();
                        notifyTransfer.setClientId(clientId);
                        notifyTransfer.setMessage(notifyMessage.toByteArray());
                        notifyTransfer.setUserId(userId);
                        RedisStandalone.REDIS.lpush(RedisConstants.MESSAGE_NOTIFY, GSONParser.getInstance().toJson(notifyTransfer));
                    });
                } else {
                    logger.info("send message fail, can not found the user session by user name ; {}", receive);
                }
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
     * @description: 获取推送消息
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
