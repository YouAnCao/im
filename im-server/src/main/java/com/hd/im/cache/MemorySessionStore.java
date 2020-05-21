package com.hd.im.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hd.im.netty.channel.NettyChannelKeys;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.UserSession;
import com.im.core.redis.RedisStandalone;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: MemorySessionStore
 * @Description: 保存用户连接信息
 * @Author: Lyon.Cao
 * @Date: 2020/5/9 14:51
 * @Version: 1.0.0
 **/
public class MemorySessionStore {

    private static MemorySessionStore memorySessionStore;

    public static MemorySessionStore getInstance() {
        if (memorySessionStore == null) {
            synchronized (MemorySessionStore.class) {
                if (memorySessionStore == null) {
                    memorySessionStore = new MemorySessionStore();
                }
            }
        }
        return memorySessionStore;
    }

    public UserSession getUserSessionOnChannel(String clientId, String userId) {
        ChannelHandlerContext channelHandlerContext = CONNECTIONS.get(clientId);
        if (channelHandlerContext != null && channelHandlerContext.channel().isActive()) {
            UserSession userSession = channelHandlerContext.channel().attr(NettyChannelKeys.USER_SESSION).get();
            if (userSession == null) {
                CONNECTIONS.remove(clientId);
                return null;
            }
            return userSession;
        }
        return null;
    }

    public List<UserSession> getUserSessionsByUserName(String user) {
        final List<UserSession> sessions = new ArrayList<>();
        /* 获取用户客户端信息 */
        String      clientsKey = String.format(RedisConstants.USER_CLIENTS, user);
        Set<String> clients    = RedisStandalone.REDIS.smembers(clientsKey);
        if (clients == null || clients.size() == 0) {
            return null;
        }
        clients.forEach((client) -> {
            /* 获取clientID对应的session信息 */
            String              clientKey = String.format(RedisConstants.USER_SESSION, client);
            Map<String, String> map       = RedisStandalone.REDIS.hgetAll(clientKey);
            if (map == null || map.size() == 0) {
                return;
            }
            String userId = map.get("userId");
            if (!user.equals(userId)) {
                // TODO clean session
                return;
            }
            UserSession session = BeanUtil.mapToBean(map, UserSession.class, true);
            sessions.add(session);
        });
        return sessions;
    }

    private final static ConcurrentHashMap<String, ChannelHandlerContext> CONNECTIONS = new ConcurrentHashMap<>();


    public boolean sendMessage(String clientId, String userId, ByteBuf payload) {
        ChannelHandlerContext channelHandlerContext = CONNECTIONS.get(clientId);
        if (channelHandlerContext != null && channelHandlerContext.channel().isActive()) {
            UserSession userSession = channelHandlerContext.channel().attr(NettyChannelKeys.USER_SESSION).get();
            if (userSession == null) {
                CONNECTIONS.remove(clientId);
                return false;
            }
            if (userId.equals(userSession.getUserId())) {
                channelHandlerContext.writeAndFlush(payload);
            }
            return true;
        }
        return false;
    }

    public void saveClient(String clientId, ChannelHandlerContext channelHandlerContext) {
        if (channelHandlerContext != null && channelHandlerContext.channel().isActive()) {
            CONNECTIONS.put(clientId, channelHandlerContext);
        }
    }

    public void tickClient(String clientId) {
        if (StrUtil.isNotEmpty(clientId)) {
            ChannelHandlerContext channelHandlerContext = CONNECTIONS.remove(clientId);
            if (channelHandlerContext != null) {
                channelHandlerContext.close();
            }
        }
    }

}
