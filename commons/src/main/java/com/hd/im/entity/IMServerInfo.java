package com.hd.im.entity;

public class IMServerInfo {

    private String ip;
    private int    port;
    public  long   timestamp;

    public IMServerInfo() {
    }

    public IMServerInfo(String ip, int port, long timestamp) {
        this.ip = ip;
        this.port = port;
        this.timestamp = timestamp;
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
