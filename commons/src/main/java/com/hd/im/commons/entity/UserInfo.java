package com.hd.im.commons.entity;

public class UserInfo {
    /**
     * 当前登录用户的Id
     */
    private Long   id;
    /**
     * 当前登录用户的手机号
     */
    private String username;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 角色ID
     */
    private Long   roleId;
    /**
     * 登录平台
     */
    private String platform;
    /**
     * ip
     */
    private String ip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
