package com.hd.im.entity;

/**
 * @ClassName: UserSession
 * @Description: 用户session
 * @Author: Lyon.Cao
 * @Date: 2020/5/9 15:00
 * @Version: 1.0.0
 **/
public class UserSession {
    private String userId;
    private String clientId;
    private String aesKey;
    private Object connect;

    public UserSession() {

    }

    public UserSession(String userId, String clientId, String aesKey, Object connect) {
        this.userId = userId;
        this.clientId = clientId;
        this.aesKey = aesKey;
        this.connect = connect;
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

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public Object getConnect() {
        return connect;
    }

    public void setConnect(Object connect) {
        this.connect = connect;
    }
}
