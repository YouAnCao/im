package com.hd.im.propertis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: JWTProperties
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/14 10:34
 * @Version: 1.0.0
 **/
@Configuration
@ConfigurationProperties(prefix = JWTProperties.JWT_PREFIX)
public class JWTProperties {
    protected static final String JWT_PREFIX = "jwt";

    private String audience;
    private String issuer;
    private String secretKey;

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
