package com.hd.im.netty.channel;


import com.im.core.entity.UserSession;
import io.netty.util.AttributeKey;

public class NettyChannelKeys {
    public static final AttributeKey<UserSession> USER_SESSION = AttributeKey.valueOf("user_session");
}
