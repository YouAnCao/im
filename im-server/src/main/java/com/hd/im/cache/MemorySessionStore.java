package com.hd.im.cache;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

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

    private final static ConcurrentHashMap<String, ChannelHandlerContext> CONNECTIONS = new ConcurrentHashMap<>();

    public void sendMessage(String clientId, byte[] data) {
        ChannelHandlerContext channelHandlerContext = CONNECTIONS.get(clientId);
        if (channelHandlerContext != null && channelHandlerContext.channel().isOpen()) {
            ByteBuf byteBuf = channelHandlerContext.alloc().buffer(data.length);
            channelHandlerContext.writeAndFlush(byteBuf);
            byteBuf.release();
        }
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
