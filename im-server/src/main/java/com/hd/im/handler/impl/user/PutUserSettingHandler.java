package com.hd.im.handler.impl.user;

import cn.hutool.core.util.StrUtil;
import com.hd.im.api.entity.UserSetting;
import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.hd.im.service.UserSettingService;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: PutUserSettingHandler
 * @Description: 新增/修改用户设置
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 14:31
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.IMCommand.USP)
public class PutUserSettingHandler extends IMHandler {

    private Logger logger = LoggerFactory.getLogger(PutUserSettingHandler.class);

    @Override
    public int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        HDIMProtocol.UserSetting userSetting = null;
        try {
            userSetting = HDIMProtocol.UserSetting.parseFrom(messagePack.getPayload().toByteArray());
        } catch (Exception e) {
            logger.error("parser message fail.", e);
            return ErrorCode.REQ_DATA_PARSER_FAIL;
        }
        UserSettingService settingService = ApplicationContextHolder.getApplicationContext().getBean("userSettingServiceImpl", UserSettingService.class);
        UserSetting        params         = new UserSetting();
        if (userSetting.getUserSettingType() > 1) {
            params.setScope(userSetting.getUserSettingType());
        }
        if (StrUtil.isNotEmpty(userSetting.getKey())) {
            params.setKey(userSetting.getKey());
        }
        if (userSetting.getDt() > 0) {
            params.setDt(userSetting.getDt());
        }
        Long dt = settingService.putUserSettings(params);
        if (dt == null || dt == 0) {

        }
        HDIMProtocol.ModifyUserSettingResponse response = HDIMProtocol.ModifyUserSettingResponse.newBuilder().setDt(dt).build();
        payload.writeBytes(response.toByteArray());
        return ErrorCode.SUCCESS;
    }
}
