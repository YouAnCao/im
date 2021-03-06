package com.im.core.utils;


import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Create By yyy on 2020/5/11
 */
public class AESHelper {

    private static Cipher cipher = null; // 私鈅加密对象Cipher

    public static void main(String args[]) {
        System.out.println("AES加解密测试：");

        String password = "c8a9229820ffa315bc6a17a9e43d01a9";
        String content  = "6222001521522152212";
        // 加密（传输)
        System.out.println("加密前：" + content);
        byte[] encryptResult = encrypt(content.getBytes(), password);

        //        // 以HEX进行传输
        //        String codedtextb = Base64.encodeToString(encryptResult,Base64.DEFAULT);// data transfer as text
        //        System.out.println("Base64 format:" + codedtextb);
        //        encryptResult = Base64.decode(codedtextb,Base64.DEFAULT);
        //
        //        // 解密
        String decryptResultb = decrypt(encryptResult, password);
        System.out.println("解密后：" + decryptResultb);
    }

    static {
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @param message
     * @return
     */
    public static byte[] encryptStr(String message, String passWord) {

        try {
            /* AES算法 */
            SecretKey secretKey = new SecretKeySpec(passWord.getBytes(), "AES");// 获得密钥
            /* 获得一个私鈅加密类Cipher，DESede-》AES算法，ECB是加密模式，PKCS5Padding是填充方式 */
            cipher.init(Cipher.ENCRYPT_MODE, secretKey); // 设置工作模式为加密模式，给出密钥
            byte[] resultBytes = cipher.doFinal(message.getBytes("UTF-8")); // 正式执行加密操作
            return resultBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(byte[] message, String passWord) {

        try {
            /* AES算法 */
            SecretKey secretKey = new SecretKeySpec(passWord.getBytes(), "AES");// 获得密钥
            /* 获得一个私鈅加密类Cipher，DESede-》AES算法，ECB是加密模式，PKCS5Padding是填充方式 */
            cipher.init(Cipher.ENCRYPT_MODE, secretKey); // 设置工作模式为加密模式，给出密钥
            byte[] resultBytes = cipher.doFinal(message); // 正式执行加密操作
            return resultBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] messageBytes, String passWord) {
        String result = "";
        try {
            /* AES算法 */
            SecretKey secretKey = new SecretKeySpec(passWord.getBytes(), "AES");// 获得密钥
            cipher.init(Cipher.DECRYPT_MODE, secretKey); // 设置工作模式为解密模式，给出密钥
            byte[] resultBytes = cipher.doFinal(messageBytes);// 正式执行解密操作
            result = new String(resultBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解密
     *
     * @param
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] messageBytes, byte[] passWord) {
        try {
            /* AES算法 */
            SecretKey secretKey = new SecretKeySpec(passWord, "AES");// 获得密钥
            cipher.init(Cipher.DECRYPT_MODE, secretKey); // 设置工作模式为解密模式，给出密钥
            byte[] resultBytes = cipher.doFinal(messageBytes);// 正式执行解密操作
            return resultBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 去掉加密字符串换行符
     *
     * @param str
     * @return
     */
    public static String filter(String str) {
        String       output = "";
        StringBuffer sb     = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            int asc = str.charAt(i);
            if (asc != 10 && asc != 13) {
                sb.append(str.subSequence(i, i + 1));
            }
        }
        output = new String(sb);
        return output;
    }
}
