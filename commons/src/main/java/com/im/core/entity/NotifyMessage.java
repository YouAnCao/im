package com.im.core.entity;

import com.google.protobuf.InvalidProtocolBufferException;
import com.im.core.proto.HDIMProtocol;

/**
 * @ClassName: NotifyMessage
 * @Description: 通知消息包体
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 18:33
 * @Version: 1.0.0
 **/
public class NotifyMessage {
    private String clientId;
    private String userId;
    private byte[] message;

    public NotifyMessage() {
    }

    public NotifyMessage(String clientId, String userId, byte[] message) {
        this.clientId = clientId;
        this.userId = userId;
        this.message = message;
    }

    public HDIMProtocol.NotifyMessage toNotifyMessage() {
        if (message != null && message.length > 0) {
            try {
                HDIMProtocol.NotifyMessage messageBody = HDIMProtocol.NotifyMessage.parseFrom(message);
                return messageBody;
            } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                invalidProtocolBufferException.printStackTrace();
            }
        }
        return null;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
