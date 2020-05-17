package com.im.core.entity;

/**
 * @ClassName: UserSession
 * @Description: 用户session
 * @Author: Lyon.Cao
 * @Date: 2020/5/9 15:00
 * @Version: 1.0.0
 **/
public class UserSession {
    private String userId;  // 用户ID
    private String clientId; // 客户端ID
    private String aesKey; // 客户端AES key
    private Long   lastActiveTime; // 最后活跃时间

    public UserSession() {

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

    public Long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
}
