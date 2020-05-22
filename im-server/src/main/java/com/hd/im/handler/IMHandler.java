package com.hd.im.handler;

import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;

/**
 * @ClassName: IMHandler
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/15 10:44
 * @Version: 1.0.0
 **/
public abstract class IMHandler {

    public abstract int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload);

    public int execute(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        return action(userSession, messagePack, payload);
    }
}
