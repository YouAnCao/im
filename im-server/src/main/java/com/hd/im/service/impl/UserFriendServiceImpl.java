package com.hd.im.service.impl;

import com.hd.im.service.UserFriendService;
import com.hd.user.api.model.ro.UserFriendSearchRO;
import com.hd.user.api.service.UserFriendApi;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: UserFriendServiceImpl
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/12 16:43
 * @Version: 1.0.0
 **/
public class UserFriendServiceImpl implements UserFriendService {
    @Autowired
    UserFriendApi userFriendApi;

    @Override
    public void getFriend() {
    }
}
