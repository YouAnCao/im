package com.hd.im.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.handler.IMHandler;
import com.hd.im.handler.annotation.Handler;
import com.hd.im.propertis.JWTProperties;
import com.hd.im.service.MessageService;
import com.im.core.constants.ErrorCode;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.AESHelper;
import com.im.core.utils.JWTUtils;
import com.im.core.utils.RSAUtils;
import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @ClassName: LoginHandler
 * @Description: 用户登录
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 10:13
 * @Version: 1.0.0
 **/
@Handler(HDIMProtocol.IMCommand.LOGIN)
public class UserLoginHandler extends IMHandler {

    private Logger logger = LoggerFactory.getLogger(UserLoginHandler.class);

    @Override
    public int action(Attribute<UserSession> userSession, HDIMProtocol.MessagePack messagePack, ByteBuf payload) {
        HDIMProtocol.Login login = null;
        try {
            login = HDIMProtocol.Login.parseFrom(messagePack.getPayload().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            logger.error("parse the pull message request fail.", e);
            return ErrorCode.REQ_DATA_PARSER_FAIL;
        }

        /* channel 上绑定的用户session信息 */
        UserSession sessionInfo = null;
        /* 已经登录过,拒绝本次连接 */
        if (userSession != null && (sessionInfo = userSession.get()) != null) {
            logger.error("user repeat login.", userSession);
            return ErrorCode.USER_REPEAT_LOGIN;
        }

        /* 1. 根据用户token,获取在路由接口中产生的RSA私钥 */
        String token = login.getToken();

        if (StrUtil.isEmpty(token)) {
            return ErrorCode.REQ_TOKEN_NOT_FOUND;
        }

        JWTProperties       jwtProperties = ApplicationContextHolder.getApplicationContext().getBean(JWTProperties.class);
        Map<String, String> userInfo      = null;
        try {
            userInfo = JWTUtils.jwtParser(jwtProperties.getSecretKey(), token, jwtProperties.getAudience(), jwtProperties.getIssuer());
            if (userInfo == null || userInfo.size() == 0) {
                return ErrorCode.REQ_BAD_TOKEN;
            }
        } catch (Exception e) {
            return ErrorCode.REQ_BAD_TOKEN;
        }
        String userInfoKey = String.format(RedisConstants.USER_INFO, userInfo.get(JWTUtils.USER_ID));
        String privateKey  = RedisStandalone.REDIS.hget(userInfoKey, RSAUtils.PRIVATE_KEY);
        if (StrUtil.isEmpty(privateKey)) {
            return ErrorCode.USER_PRI_KEY_NOT_EXIST;
        }
        String aesKey = null;
        try {
            aesKey = RSAUtils.decryptByPriKey(login.getEncryptKey().toString("utf-8"), privateKey);
        } catch (Exception e) {
            return ErrorCode.DECRYPT_KEY_FAIL;
        }
        String decrypt = null;
        try {
            decrypt = AESHelper.decrypt(login.getEncryptData().toByteArray(), aesKey);
        } catch (Exception e) {
            return ErrorCode.DECRYPT_DATA_FAIL;
        }
        HDIMProtocol.ClientInfo clientInfo = null;
        try {
            clientInfo = HDIMProtocol.ClientInfo.parseFrom(decrypt.getBytes("utf-8"));
        } catch (Exception e) {
            return ErrorCode.PARSER_CLIENT_INFO_FAIL;
        }

        UserSession currentUserSession = new UserSession();
        currentUserSession.setClientId(clientInfo.getClientId());
        currentUserSession.setUserId(clientInfo.getUserId());
        currentUserSession.setAesKey(aesKey);

        /* 根据用户ID,保存用户clientID集合 */
        String userClientCache = String.format(RedisConstants.USER_CLIENTS, clientInfo.getUserId());
        RedisStandalone.REDIS.sadd(userClientCache, clientInfo.getClientId());
        String userSessionCache = String.format(RedisConstants.USER_SESSION, clientInfo.getClientId());
        long   lastActiveTime   = System.currentTimeMillis();
        Map    map              = BeanUtil.beanToMap(currentUserSession, false, true);
        map.put("lastActiveTime", String.valueOf(lastActiveTime));
        RedisStandalone.REDIS.hmset(userSessionCache, map);

        currentUserSession.setLastActiveTime(lastActiveTime);
        userSession.setIfAbsent(currentUserSession);

        MessageService             messageServiceImpl = ApplicationContextHolder.getApplicationContext().getBean("messageServiceImpl", MessageService.class);
        long                       messageHead        = messageServiceImpl.getMessageHead(currentUserSession.getUserId());
        HDIMProtocol.LoginResponse loginResponse      = HDIMProtocol.LoginResponse.newBuilder().setMessageHead(messageHead).setUserFriendHead(-1L).setUserSettingHead(-1L).setFriendRequestHead(-1L).build();

        payload.writeBytes(loginResponse.toByteArray());
        return ErrorCode.SUCCESS;
    }
}
