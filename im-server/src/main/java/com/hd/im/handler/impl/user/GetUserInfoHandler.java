package com.hd.im.handler.impl.user;

import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;

/**
 * @ClassName: GetUserInfoHandler
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/22 19:57
 * @Version: 1.0.0
 **/
public class GetUserInfoHandler extends IMHandler {
    @Override
    public int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        
        return 0;
    }
}
