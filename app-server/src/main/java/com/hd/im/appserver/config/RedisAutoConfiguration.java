package com.hd.im.appserver.config;

import com.hd.im.appserver.properties.RedisProperties;
import com.hd.im.commons.redis.RedisConfig;
import com.hd.im.commons.redis.RedisStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisAutoConfiguration {

    @Autowired
    RedisProperties properties;

    @Bean
    public RedisStandalone redisStandalone() {
        RedisConfig.CONFIGINSTANCE.setHost(properties.getHost());
        RedisConfig.CONFIGINSTANCE.setPassword(properties.getPassword());
        RedisConfig.CONFIGINSTANCE.setPort(properties.getPort());
        RedisConfig.CONFIGINSTANCE.setMaxIdle(properties.getMaxIdle());
        RedisConfig.CONFIGINSTANCE.setMaxTotal(properties.getMaxTotal());
        RedisConfig.CONFIGINSTANCE.setMaxWait(properties.getMaxWait());
        RedisConfig.CONFIGINSTANCE.setMinIdle(properties.getMinIdle());

        RedisStandalone.REDIS.init(RedisConfig.CONFIGINSTANCE);
        return RedisStandalone.REDIS;
    }

}
