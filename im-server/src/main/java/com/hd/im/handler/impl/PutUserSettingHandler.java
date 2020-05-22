package com.hd.im.handler.impl;

import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: PutUserSettingHandler
 * @Description: 新增用户设置
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 14:31
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.IMCommand.USP)
public class PutUserSettingHandler extends IMHandler {

    private Logger logger = LoggerFactory.getLogger(PutUserSettingHandler.class);

    @Override
    public int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        return 0;
    }
}
