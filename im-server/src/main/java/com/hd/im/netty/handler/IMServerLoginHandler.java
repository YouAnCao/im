package com.hd.im.netty.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hd.im.cache.MemorySessionStore;
import com.hd.im.netty.channel.NettyChannelKeys;
import com.hd.im.service.MessageService;
import com.im.core.constants.RedisConstants;
import com.im.core.utils.RSAUtils;
import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.propertis.JWTProperties;
import com.im.core.constants.ErrorCode;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.AESHelper;
import com.im.core.utils.JWTUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class IMServerLoginHandler extends SimpleChannelInboundHandler<HDIMProtocol.Login> {

    Logger logger = LoggerFactory.getLogger(IMServerLoginHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HDIMProtocol.Login msg) throws Exception {
        // 2. 根据RSA私钥，解密用户提交的AES加密数据
        // 3. 将解密出来的AES密钥解密提交的加密数据
        // 4. 比对加密数据与用户token对应的基础数据，判断是否登录成功
        // 5. 如果登录成功，判断当前缓存是否存在[设备号]对应的连接信息
        // 6. 如果设备号对应的连接存在，强制断开连接
        // 7. 写入用户基本数据，用户ID对应一个设备号
        // 8. 写入设备号对应数据，设备号对应一个用户session
        // 9. 响应给客户端用户基础信息 用户消息头/用户好友消息头/用户设置消息头/好友请求消息头

        /* 响应数据 */
        ByteBuf result = ctx.alloc().buffer();
        result.writeByte(HDIMProtocol.HeadType.LOGIN_RESPONSE.getNumber());

        /* 错误码 */
        int errorCode = ErrorCode.SUCCESS;

        /* channel 上绑定的用户session信息 */
        Attribute<UserSession> session     = ctx.channel().attr(NettyChannelKeys.USER_SESSION);
        UserSession            sessionInfo = null;
        /* 已经登录过,拒绝本次连接 */
        if (session != null && (sessionInfo = session.get()) != null) {
            logger.error("user repeat login.", session);
            errorCode = ErrorCode.USER_REPEAT_LOGIN;
            return;
        }

        /* 1. 根据用户token,获取在路由接口中产生的RSA私钥 */
        String token = msg.getToken();
        try {
            if (StrUtil.isEmpty(token)) {
                errorCode = ErrorCode.REQ_TOKEN_NOT_FOUND;
                return;
            }

            JWTProperties       jwtProperties = ApplicationContextHolder.getApplicationContext().getBean(JWTProperties.class);
            Map<String, String> userInfo      = null;
            try {
                userInfo = JWTUtils.jwtParser(jwtProperties.getSecretKey(), token, jwtProperties.getAudience(), jwtProperties.getIssuer());
                if (userInfo == null || userInfo.size() == 0) {
                    errorCode = ErrorCode.REQ_BAD_TOKEN;
                    return;
                }
            } catch (Exception e) {
                errorCode = ErrorCode.REQ_BAD_TOKEN;
                return;
            }
            String userInfoKey = String.format(RedisConstants.USER_INFO, userInfo.get(JWTUtils.USER_ID));
            String privateKey  = RedisStandalone.REDIS.hget(userInfoKey, RSAUtils.PRIVATE_KEY);
            if (StrUtil.isEmpty(privateKey)) {
                errorCode = ErrorCode.USER_PRI_KEY_NOT_EXIST;
                return;
            }
            String aesKey = null;
            try {
                aesKey = RSAUtils.decryptByPriKey(msg.getEncryptKey().toString("utf-8"), privateKey);
            } catch (Exception e) {
                errorCode = ErrorCode.DECRYPT_KEY_FAIL;
                return;
            }
            String decrypt = null;
            try {
                decrypt = AESHelper.decrypt(msg.getEncryptData().toByteArray(), aesKey);
            } catch (Exception e) {
                errorCode = ErrorCode.DECRYPT_DATA_FAIL;
                return;
            }
            HDIMProtocol.ClientInfo clientInfo = null;
            try {
                clientInfo = HDIMProtocol.ClientInfo.parseFrom(decrypt.getBytes("utf-8"));
            } catch (Exception e) {
                errorCode = ErrorCode.PARSER_CLIENT_INFO_FAIL;
                return;
            }

            UserSession userSession = new UserSession();
            userSession.setClientId(clientInfo.getClientId());
            userSession.setUserId(clientInfo.getUserId());
            userSession.setAesKey(aesKey);

            /* 根据用户ID,保存用户clientID集合 */
            String userClientCache = String.format(RedisConstants.USER_CLIENTS, clientInfo.getUserId());
            RedisStandalone.REDIS.sadd(userClientCache, clientInfo.getClientId());
            String userSessionCache = String.format(RedisConstants.USER_SESSION, clientInfo.getClientId());
            long   lastActiveTime   = System.currentTimeMillis();
            Map    map              = BeanUtil.beanToMap(userSession, false, true);
            map.put("lastActiveTime", String.valueOf(lastActiveTime));
            RedisStandalone.REDIS.hmset(userSessionCache, map);

            userSession.setLastActiveTime(lastActiveTime);
            session.setIfAbsent(userSession);

            MemorySessionStore.getInstance().saveClient(clientInfo.getClientId(), ctx);

            MessageService             messageServiceImpl = ApplicationContextHolder.getApplicationContext().getBean("messageServiceImpl", MessageService.class);
            long                       messageHead        = messageServiceImpl.getMessageHead(userSession.getUserId());
            HDIMProtocol.LoginResponse loginResponse      = HDIMProtocol.LoginResponse.newBuilder().setMessageHead(messageHead).setUserFriendHead(-1L).setUserSettingHead(-1L).setFriendRequestHead(-1L).build();

            byte[] data    = loginResponse.toByteArray();
            byte[] encrypt = AESHelper.encrypt(data, aesKey);
            result.writeByte(ErrorCode.SUCCESS);
            result.writeBytes(encrypt);
        } finally {
            if (errorCode != ErrorCode.SUCCESS) {
                logger.error("login fail result: {}, client info: {}", errorCode, ctx.channel().remoteAddress());
                result.writeByte(errorCode);
            }
            ctx.writeAndFlush(result);
        }
    }
}
