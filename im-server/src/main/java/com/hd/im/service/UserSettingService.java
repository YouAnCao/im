package com.hd.im.service;

import com.hd.im.api.entity.UserSetting;
import com.im.core.proto.HDIMProtocol;

import java.util.List;

/**
 * @ClassName: UserSettingService
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 20:55
 * @Version: 1.0.0
 **/
public interface UserSettingService {
    public List<HDIMProtocol.UserSetting> getUserSettings(UserSetting userSetting);

    public Long putUserSettings(UserSetting userSetting);
}
