package com.hd.im.handler;

import cn.hutool.core.lang.UUID;
import com.hd.im.cache.MemorySessionStore;
import com.hd.im.channel.NettyChannelKeys;
import com.hd.im.entity.UserSession;
import com.hd.im.proto.HDIMProtocol;
import com.hd.im.proto.HDIMProtocol.Login;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;

public class IMServerLoginHandler extends SimpleChannelInboundHandler<HDIMProtocol.Login> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Login msg) throws Exception {
        // TODO 完成登录验证
        // 1. 根据用户token,获取在路由接口中产生的RSA私钥
        // 2. 根据RSA私钥，解密用户提交的 AES 加密数据
        // 3. 将解密出来的AES密钥解密提交的加密数据
        // 4. 比对加密数据与用户token对应的基础数据，判断是否登录成功
        // 5. 如果登录成功，判断当前缓存是否存在[设备号]对应的连接信息
        // 6. 如果设备号对应的连接存在，强制断开连接
        // 7. 写入用户基本数据，用户ID对应一个设备号
        // 8. 写入设备号对应数据，设备号对应一个用户session
        // 9. 响应给客户端用户基础信息 用户消息头/用户好友消息头/用户设置消息头/好友请求消息头

        String                 token   = msg.getToken();
        String                 uuid    = UUID.fastUUID().toString();
        Attribute<UserSession> session = ctx.channel().attr(NettyChannelKeys.USER_SESSION);
        session.setIfAbsent(new UserSession("user", uuid, "aes key", null));
        MemorySessionStore.getInstance().saveClient(uuid, ctx);

        byte[] data = HDIMProtocol.LoginResponse.newBuilder().setMessageHead(0L).setUserFriendHead(0L)
                .setUserSettingHead(0L).setFriendRequestHead(0L).build().toByteArray();
        /* 响应数据 */
        ByteBuf byteBuf = ctx.alloc().buffer(data.length);
    }
}
