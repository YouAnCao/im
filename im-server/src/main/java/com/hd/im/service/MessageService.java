package com.hd.im.service;

import com.im.core.constants.ErrorCode;
import com.im.core.entity.MessageBundle;
import com.im.core.entity.PublishMessage;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;

/**
 * 消息服务 by Lyon.Cao 2020年5月15日10:29:05
 */
public interface MessageService {

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
    public void storeMessage(MessageBundle messageBundle);

    /**
     * @param payload
     * @return
     * @description 推送消息
     */
    public int sendMessage(PublishMessage publishMessage);

}
