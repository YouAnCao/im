package com.hd.im.appserver.controller;

import com.hd.im.commons.dto.Result;
import com.hd.im.commons.entity.IMServerInfo;
import com.hd.im.commons.redis.RedisStandalone;
import com.hd.im.commons.utils.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/router")
public class RouterController {

    Logger logger = LoggerFactory.getLogger(RouterController.class);

    @GetMapping
    public Result getIMServerInfo(HttpServletRequest request) {
        String token = request.getHeader("X-User-Token");
        if (token == null) {
            return Result.fail(1, "the token can not be null.");
        }
        SecureRandom        secureRandom = new SecureRandom();
        Map<String, String> keys         = null;
        try {
            keys = RSAUtils.initKey(secureRandom.getAlgorithm());
        } catch (Exception e) {
            logger.error("", e);
            return Result.fail(2, "the rsa key generate fail");
        }
        String publicKey  = keys.get(RSAUtils.PUBLIC_KEY);
        String privateKey = keys.get(RSAUtils.PRIVATE_KEY);
        /* 缓存私钥 */
        RedisStandalone.REDIS.hset(token, "rsa_private", privateKey);
        return Result.ok(new IMServerInfo("192.168.0.126", 2020, publicKey, System.currentTimeMillis()));
    }
}
