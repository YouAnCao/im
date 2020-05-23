package com.hd.im.service.impl;

import com.hd.im.api.entity.FriendInfo;
import com.hd.im.api.service.UserFriendApi;
import com.hd.im.service.UserFriendService;
import com.im.core.proto.HDIMProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UserFriendServiceImpl
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/12 16:43
 * @Version: 1.0.0
 **/
@Service
public class UserFriendServiceImpl implements UserFriendService {

    @Autowired
    UserFriendApi userFriendApi;

    @Override
    public List<HDIMProtocol.Friend> getFriends(String uid, Long userFriendHead) {
        List<HDIMProtocol.Friend> friends     = new ArrayList<>();
        List<FriendInfo>          userFriends = userFriendApi.getUserFriends(uid);
        if (userFriends != null && userFriends.size() > 0) {
            userFriends.forEach(userFriend -> {
                HDIMProtocol.Friend.Builder builder = HDIMProtocol.Friend.newBuilder();
                builder.setAlias(userFriend.getAlias());
                builder.setUid(userFriend.getFriendUid());
                builder.setFriendOrigin(userFriend.getFriendOrigin());
                builder.setState(userFriend.getState());
                friends.add(builder.build());
            });
        }
        return friends;
    }
}
