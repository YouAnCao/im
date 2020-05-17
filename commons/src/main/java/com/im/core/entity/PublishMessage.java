package com.im.core.entity;

import com.google.protobuf.InvalidProtocolBufferException;
import com.im.core.proto.HDIMProtocol;

/**
 * @ClassName: PublishMessage
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 17:26
 * @Version: 1.0.0
 **/
public class PublishMessage {

    private String userId;
    private String clientId;
    private byte[] message;

    public PublishMessage(String userId, String clientId, HDIMProtocol.Message message) {
        this.userId = userId;
        this.clientId = clientId;
        this.message = message.toByteArray();
    }

    public HDIMProtocol.Message toMessage() {
        if (message != null && message.length > 0) {
            try {
                HDIMProtocol.Message messageBody = HDIMProtocol.Message.parseFrom(this.message);
                return messageBody;
            } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                invalidProtocolBufferException.printStackTrace();
            }
        }
        return null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
