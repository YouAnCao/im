package com.im.core.entity;

public class IMServerInfo {

    private String ip;
    private int    port;
    private String encryptKey;
    public  long   timestamp;

    public IMServerInfo() {
    }

    public IMServerInfo(String ip, int port, long timestamp) {
        this.ip = ip;
        this.port = port;
        this.timestamp = timestamp;
    }

    public IMServerInfo(String ip, int port, String encryptKey, long timestamp) {
        this.ip = ip;
        this.port = port;
        this.encryptKey = encryptKey;
        this.timestamp = timestamp;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
