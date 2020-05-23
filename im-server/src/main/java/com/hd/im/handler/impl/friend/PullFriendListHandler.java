package com.hd.im.handler.impl.friend;

import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.hd.im.service.UserFriendService;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName: PullFriendHandler
 * @Description: 拉取好友列表
 * @Author: Lyon.Cao
 * @Date: 2020/5/22 17:44
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.IMCommand.FP)
public class PullFriendListHandler extends IMHandler {

    private Logger logger = LoggerFactory.getLogger(PullFriendListHandler.class);

    @Override
    public int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        HDIMProtocol.GetFriendsRequest getFriendsRequest = null;
        try {
            getFriendsRequest = HDIMProtocol.GetFriendsRequest.parseFrom(messagePack.getPayload().toByteArray());
        } catch (Exception e) {
            logger.error("parser message fail.", e);
            return ErrorCode.REQ_DATA_PARSER_FAIL;
        }
        long                            userFriendHead        = getFriendsRequest.getUserFriendHead();
        UserFriendService               userFriendServiceImpl = ApplicationContextHolder.getApplicationContext().getBean("userFriendServiceImpl", UserFriendService.class);
        List<HDIMProtocol.Friend>       friends               = userFriendServiceImpl.getFriends(userSession.get().getUserId(), userFriendHead);
        HDIMProtocol.GetFriendsResponse friendsResponse       = HDIMProtocol.GetFriendsResponse.newBuilder().addAllFriends(friends).build();
        payload.writeBytes(friendsResponse.toByteArray());
        return ErrorCode.SUCCESS;
    }
}
