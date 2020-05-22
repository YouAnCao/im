package com.hd.im.service.impl;

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
    public List<HDIMProtocol.UserSetting> getUserSettings(int scope, String key);
}
