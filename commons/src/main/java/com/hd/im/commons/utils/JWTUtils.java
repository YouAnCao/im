package com.hd.im.commons.utils;

import io.jsonwebtoken.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: JWTUtils
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/14 10:02
 * @Version: 1.0.0
 **/
public class JWTUtils {
    public static final String USER_ID   = "userId";
    public static final String USERNAME  = "username";
    public static final String REAL_NAME = "realName";
    public static final String ROLE_ID   = "roleId";
    public static final String PLATFORM  = "platform";
    public static final String IP        = "ip";

    public JWTUtils() {
    }

    public static Map<String, String> jwtParser(String signKey, String token, String audience, String issuer) {
        Map<String, String> result    = null;
        Jws<Claims>         claimsJws = Jwts.parser().setSigningKey(signKey).requireAudience(audience).requireIssuer(issuer).parseClaimsJws(token);
        if (claimsJws != null) {
            result = new HashMap();
            result.put("userId", ((Claims) claimsJws.getBody()).getId());
            result.put("username", ((Claims) claimsJws.getBody()).get("username", String.class));
            result.put("realName", ((Claims) claimsJws.getBody()).get("realName", String.class));
            result.put("roleId", ((Claims) claimsJws.getBody()).get("roleId", String.class));
            result.put("platform", ((Claims) claimsJws.getBody()).get("platform", String.class));
            result.put("ip", ((Claims) claimsJws.getBody()).get("ip", String.class));
        }

        return result;
    }
}
