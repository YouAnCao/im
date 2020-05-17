package com.im.core.entity;

import com.google.protobuf.InvalidProtocolBufferException;
import com.im.core.proto.HDIMProtocol;

/**
 * @ClassName: MessageBundle
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 14:43
 * @Version: 1.0.0
 **/
public class MessageBundle {
    private long   messageId;
    private String fromUser;
    private String fromClient;
    private byte[] message;

    public MessageBundle(long messageId, String fromUser, String fromClient, HDIMProtocol.Message message) {
        this.messageId = messageId;
        this.fromUser = fromUser;
        this.fromClient = fromClient;
        this.message = message.toByteArray();
    }

    public HDIMProtocol.Message toMessage() {
        if (this.message != null && this.message.length > 0) {
            HDIMProtocol.Message messageBody = null;
            try {
                messageBody = HDIMProtocol.Message.parseFrom(message);
            } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                invalidProtocolBufferException.printStackTrace();
            }
            return messageBody;
        }
        return null;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromClient() {
        return fromClient;
    }

    public void setFromClient(String fromClient) {
        this.fromClient = fromClient;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
