package com.hd.im.handler;

import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;

/**
 * @ClassName: IMHandler
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/15 10:44
 * @Version: 1.0.0
 **/
public abstract class IMHandler {

    public abstract int action(UserSession userSession, HDIMProtocol.Publish publish, ByteBuf payload);

    public int execute(UserSession userSession, HDIMProtocol.Publish publish, ByteBuf payload) {
        return action(userSession, publish, payload);
    }
}
