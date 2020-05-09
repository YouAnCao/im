package com.hd.im.proto;

import cn.hutool.core.codec.Base64;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hd.im.utils.AESUtils;

public class Tester {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        HDIMProtocol.Login login = HDIMProtocol.Login.newBuilder().setToken("token")
                .setEncryptData(ByteString.copyFrom("encrypt".getBytes()))
                .setEncryptKey(ByteString.copyFrom("key".getBytes())).build();
        byte[] encrypt = AESUtils.encrypt(login.toByteArray(), "123456");
        String encode = Base64.encode(encrypt);
        System.out.println("加密后内容：" + encode);
        System.out.println("开始解密");
        byte[] decrypt = AESUtils.decrypt(Base64.decode(encode), "123456");
        HDIMProtocol.Login login1 = HDIMProtocol.Login.parseFrom(decrypt);
        System.out.println(login1);
    }
}
