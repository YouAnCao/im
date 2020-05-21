package com.hd.im.service;

import com.im.core.constants.ErrorCode;
import com.im.core.entity.MessageBundle;
import com.im.core.entity.PublishMessage;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * 消息服务 by Lyon.Cao 2020年5月15日10:29:05
 */
public interface MessageService {

    public long getMessageHead(String user);

    /**
     * @param message
     * @return
     * @description 验证消息权限
     */
    public boolean verifySendPermissions(HDIMProtocol.Message message);

    /**
     * @param message
     * @return
     * @description 存储消息
     */
    public void storeMessage(String userId, String clientId, Long messageId, HDIMProtocol.Message message);

    /**
     * @param userId
     * @param clientId
     * @param messageSeq
     * @description 存储用户个人信息
     */
    public void storeUserMessage(String userId, Long messageId, Long messageSeq);

    /**
     * @param payload
     * @return
     * @description 推送消息
     */
    public int sendMessage(PublishMessage publishMessage);


    /**
     * @param clientId
     * @param userId
     * @param requestHead
     * @return
     * @description 获取消息内容
     */
    public HDIMProtocol.PullMessageResponse getMessage(String clientId, String userId, Long requestHead);
}
