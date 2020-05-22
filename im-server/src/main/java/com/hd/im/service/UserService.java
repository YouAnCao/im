package com.hd.im.service;

import com.im.core.proto.HDIMProtocol;
import io.netty.channel.ChannelHandlerContext;

public interface UserService {
    public int userLogin(ChannelHandlerContext ctx, HDIMProtocol.MessagePack messagePack);
}
