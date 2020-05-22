package com.hd.im.handler.impl;

import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;

/**
 * @ClassName: PingHandler
 * @Description: 心跳
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 11:51
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.IMCommand.PING)
public class ServerHeartbeatHandler extends IMHandler {

    @Override
    public int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        return ErrorCode.SUCCESS;
    }
}
