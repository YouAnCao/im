package com.im.core.constants;

public class RedisConstants {

    public static final String USER_INFO          = "im:user:%s";
    public static final String USER_CLIENTS       = "im:user:client:%s";
    public static final String USER_SESSION       = "im:user:session:%s";
    public static final String MESSAGE_STORE      = "im:message:%s";
    public static final String USER_MESSAGE_INBOX = "im:user:message:inbox:%s";
    public static final String USER_MESSAGE_HEAD  = "im:user:message:head:%s:%s";

    /* 消息拉取 量比较大需要单独处理 */
    public static final String MESSAGE_PULL    = "im:worker:queue:mp";
    /* 消息发送 量比较大需要单独处理 */
    public static final String MESSAGE_SEND    = "im:worker:queue:ms";
    /* 消息推送 */
    public static final String MESSAGE_PUBLISH = "im:worker:queue:publish";
    /* 分拣队列 */
    public static final String MESSAGE_SORTING = "im:worker:queue:sorting";
    /* 响应队列 */
    public static final String MESSAGE_RELAY   = "im:boos:queue:relay";
    /* 消息通知 */
    public static final String MESSAGE_NOTIFY  = "im:boos:queue:notify";
}
