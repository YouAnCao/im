package com.hd.im.commons.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {
    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param password 加密需要的密码
     * @return 密文
     */
    public static byte[] encrypt(byte[] content, String password) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
        kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
        //加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行
        SecretKey     secretKey    = kgen.generateKey();// 根据用户密码，生成一个密钥
        byte[]        enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
        SecretKeySpec key          = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
        Cipher        cipher       = Cipher.getInstance("AES");// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
        byte[] result = cipher.doFinal(content);// 加密
        return result;
    }

    private Cipher getCipher(String password) {
        return null;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param content  AES加密过过的内容
     * @param password 加密时的密码
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, String password) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
        kgen.init(128, new SecureRandom(password.getBytes()));
        SecretKey     secretKey    = kgen.generateKey();// 根据用户密码，生成一个密钥
        byte[]        enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
        SecretKeySpec key          = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
        Cipher        cipher       = Cipher.getInstance("AES");// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
        byte[] result = cipher.doFinal(content);
        return result; // 明文
    }

    public static void main(String[] args) throws Exception {
        String content  = "hello world!";
        String password = "123321";
        System.out.println("需要加密的内容：" + content);
        byte[] encrypt     = encrypt(content.getBytes(), password);
        String encryptData = new String(Base64.getEncoder().encode(encrypt));
        System.out.println("加密完成后:" + encryptData);
        encrypt = Base64.getDecoder().decode(encryptData);
        byte[] decrypt = decrypt(encrypt, password);
        System.out.println("解密完成:" + new String(decrypt));
    }
}