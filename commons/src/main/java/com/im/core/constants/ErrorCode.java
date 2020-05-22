package com.im.core.constants;

/**
 * @ClassName: ErrorCode
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/11 17:09
 * @Version: 1.0.0
 **/
public class ErrorCode {

    public static final int NO_RESPONSE                = -1; /* 不需要响应 */
    public static final int SUCCESS                    = 0;
    public static final int SUCCESS_ZIP                = 1;
    public static final int REQ_DATA_PARSER_FAIL       = 2;
    public static final int REQ_LOGIN_DATA_DECODE_FAIL = 3;

    /* 登录异常 */
    public static final int REQ_USER_NO_LOGIN       = 10;
    public static final int REQ_TOKEN_NOT_FOUND     = 11;
    public static final int REQ_BAD_TOKEN           = 12;
    public static final int REQ_TOKEN_HAS_EXPIRE    = 13;
    public static final int USER_PRI_KEY_NOT_EXIST  = 14;
    public static final int DECRYPT_KEY_FAIL        = 15;
    public static final int DECRYPT_DATA_FAIL       = 16;
    public static final int PARSER_CLIENT_INFO_FAIL = 17;
    public static final int USER_REPEAT_LOGIN       = 18;

    /* 推送 */
    public static final int UNKNOWN_PUBLISH_TYPE = 20;
}
