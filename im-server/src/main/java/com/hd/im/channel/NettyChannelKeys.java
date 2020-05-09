package com.hd.im.channel;

import com.hd.im.entity.UserSession;

import io.netty.util.AttributeKey;

public class NettyChannelKeys {
	public static final AttributeKey<UserSession> USER_SESSION = AttributeKey.valueOf("user_session");
}
