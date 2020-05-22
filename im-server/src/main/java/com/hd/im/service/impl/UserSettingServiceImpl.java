package com.hd.im.service.impl;

import com.hd.im.api.entity.UserSetting;
import com.hd.im.api.service.UserSettingApi;
import com.hd.im.service.UserSettingService;
import com.im.core.proto.HDIMProtocol;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UserSettingServiceImpl
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 20:55
 * @Version: 1.0.0
 **/
public class UserSettingServiceImpl implements UserSettingService {

    @Autowired
    UserSettingApi userSettingApi;

    @Override
    public List<HDIMProtocol.UserSetting> getUserSettings(UserSetting userSetting) {
        List<HDIMProtocol.UserSetting> results      = new ArrayList<>();
        List<UserSetting>              userSettings = userSettingApi.getUserSettings(userSetting);
        if (userSettings != null && userSettings.size() > 0) {
            userSettings.forEach(us -> {
                HDIMProtocol.UserSetting.Builder builder = HDIMProtocol.UserSetting.newBuilder();
                builder.setDt(us.getDt());
                builder.setKey(us.getKey());
                builder.setValue(us.getValue());
                builder.setUserSettingType(us.getScope());
                results.add(builder.build());
            });
        }
        return results;
    }

    @Override
    public Long putUserSettings(UserSetting userSetting) {
        return userSettingApi.modifyUserSetting(userSetting);
    }
}
