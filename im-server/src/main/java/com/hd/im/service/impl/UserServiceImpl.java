package com.hd.im.service.impl;

import com.hd.im.service.UserService;
import com.im.core.constants.ErrorCode;
import com.im.core.proto.HDIMProtocol;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

/**
 * @ClassName: UserServiceImpl
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/12 16:32
 * @Version: 1.0.0
 **/
@Service
public class UserServiceImpl implements UserService {

    @Override
    public int userLogin(ChannelHandlerContext ctx, HDIMProtocol.MessagePack messagePack) {
        return ErrorCode.SUCCESS;
    }
}
