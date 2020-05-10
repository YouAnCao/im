package com.hd.im.handler;

import com.hd.im.cache.MemorySessionStore;
import com.hd.im.channel.NettyChannelKeys;
import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.entity.UserSession;
import com.hd.im.proto.HDIMProtocol;
import com.hd.im.proto.HDIMProtocol.Login;

import cn.hutool.core.lang.UUID;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;

public class IMServerLoginHandler extends SimpleChannelInboundHandler<HDIMProtocol.Login> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Login msg) throws Exception {

        /* 获取token */
        String token = msg.getToken();
        //TODO 解密token获取用户ID与ClientId

        String                 uuid    = UUID.fastUUID().toString();
        Attribute<UserSession> session = ctx.channel().attr(NettyChannelKeys.USER_SESSION);
        session.setIfAbsent(new UserSession("user", uuid, "aes key", null));
        MemorySessionStore.getInstance().saveClient(uuid, ctx);

        byte[] data = HDIMProtocol.LoginResponse.newBuilder().setMessageHead(0L).setUserFriendHead(0L)
                .setUserSettingHead(0L).setFriendRequestHead(0L).build().toByteArray();
    }

}
