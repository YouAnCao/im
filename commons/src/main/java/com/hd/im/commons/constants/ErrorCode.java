package com.hd.im.commons.constants;

/**
 * @ClassName: ErrorCode
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/11 17:09
 * @Version: 1.0.0
 **/
public class ErrorCode {
    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    /**
     * 成功/压缩
     */
    public static final int SUCCESS_ZIP = 1;

    /**
     * 请求数据包解析失败
     */
    public static final int REQ_DATA_PARSER_FAIL = 2;

    /**
     * 登录失败，用户TOKEN不存在
     */
    public static final int REQ_TOKEN_NOT_FOUND = 3;

    /**
     * 沒有找到用户私钥
     */
    public static final int USER_RSA_PRI_KEY_NOT_FOUND = 4;

    /**
     * 解密AES秘钥失败
     */
    public static final int DECRYPT_AES_KEY_FAIL = 5;

    /**
     * 解密AES加密后的数据失败
     */
    public static final int DECRYPT_DATA_FAIL = 6;

    /**
     * 重复登录异常
     */
    public static final int USER_REPEAT_LOGIN = 7;
}
