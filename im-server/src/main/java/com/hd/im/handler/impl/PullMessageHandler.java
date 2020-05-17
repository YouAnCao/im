package com.hd.im.handler.impl;

import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;

/**
 * @ClassName: PullMessageHandler
 * @Description: 拉取消息
 * @Author: Lyon.Cao
 * @Date: 2020/5/15 10:53
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.PublishType.MP)
public class PullMessageHandler extends IMHandler {

    @Override
    public int action(UserSession userSession, HDIMProtocol.Publish publish, ByteBuf payload) {
        return 0;
    }
}
