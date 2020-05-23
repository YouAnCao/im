package com.hd.im.service;

import com.im.core.proto.HDIMProtocol;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserFriendService {
    public List<HDIMProtocol.Friend> getFriends(String uid, Long userFriendHead);
}
