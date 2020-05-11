package com.hd.im.service;

import cn.hutool.core.codec.Base64;
import com.google.protobuf.ByteString;
import com.hd.im.commons.proto.HDIMProtocol;
import com.hd.im.commons.utils.AESHelper;
import com.hd.im.commons.utils.AESUtils;
import com.hd.im.commons.utils.RSAUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap         bootstrap   = new Bootstrap();
        bootstrap.group(workerGroup);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {

            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("active");
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("response :" + msg);
                    }

                });
            }
        });

        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 2020)).sync();
        HDIMProtocol.ClientInfo android    = HDIMProtocol.ClientInfo.newBuilder().setClientId("cid:qwer").setUserId("uid:admin").setPlatForm(HDIMProtocol.PlatForm.ANDROID.getNumber()).setAppVersion("1.0.0").build();
        String password = "c8a9229820ffa315bc6a17a9e43d01a9";
        byte[]                  encrypt = AESHelper.encrypt(android.toByteArray(),password);
        //byte[]                  encrypt    = AESUtils.encrypt(android.toByteArray(), "123321");
        String                  encryptKey = null;
        try {
            encryptKey = RSAUtils.encryptByPubKey(password, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpJVHNq01G4htbl/bLVHXtJwAymaSa04hilhdyQ+ANdtdDcYWKHoY5uqLqnuIxQAoYPwx+eDs46DGQhc5FqBkKfZ2oA/j/72hD4QzwoCwSUA5EGwdieuqwTkJALvAxwbuMJccWZ0XAl2g2AHyU7ox3R8eDEenwM4PhCK4dpaVUlwIDAQAB");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" 加密AES密钥： " + encryptKey);
        String decryptByPriKey = RSAUtils.decryptByPriKey(encryptKey, "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKklUc2rTUbiG1uX9stUde0nADKZpJrTiGKWF3JD4A1210NxhYoehjm6ouqe4jFAChg/DH54OzjoMZCFzkWoGQp9nagD+P/vaEPhDPCgLBJQDkQbB2J66rBOQkAu8DHBu4wlxxZnRcCXaDYAfJTujHdHx4MR6fAzg+EIrh2lpVSXAgMBAAECgYAQEZ2N3mI/A6f5sZwegO2bFANuyy3aD5J7WyrrDguw+8NDgvtevbuerF5hz6pJZ34OA59aPG3T4CslQbDw6SFHt+5cl4fBcGSzbugWuLqvV3Leg+TvkS1GtyPffwEu7mM1xgS608iZz+GM1fpzo06Sa/7HggCovxNoaOzS+CuOYQJBAO9a5HHw6R/Xs3+narKkeXthreZNs4/K0a5HE3uSeOV0BHxBL/kRYXewKhjk6nz9qmc7x1pzmFfH9WXSAXMz4SkCQQC06Ief/1UOokB2ohBhGGlLALcmr4IYZtfyW3JRekK0jcCu7g7ej+qogNY5XTZBHMAbdaHcylt8099JgEHR9X+/AkAPK9mzBywfopJ0Eu6GaDpzAZv+LwqkoJxtyX+Io54aHSJzpcYeqWVeKsJgVZIvXfzBEl7ucslDNx9s1sTUwoMRAkAM+VAgrVMQRZqR53V9QR7r/hUkdRnDXjzGaMB+D2KRSEk1QmVWmMOfaf3zBzhI2yL71SfSicCd5yPwRX5y7ERJAkEAkyRFiSlPW2DEx0znszSnejMs91OlH1HNgZ+lkPics1U0DAMRnvnYhwprPJSlYkEnnLABAjWGBjjUiJK4C1tgvQ==");
        System.out.println(" 解密后AES密钥:" + decryptByPriKey);
        HDIMProtocol.Login login = HDIMProtocol.Login.newBuilder().setToken("123456").
                setEncryptData(ByteString.copyFrom(encrypt)).
                setEncryptKey(ByteString.copyFrom(encryptKey, "utf-8")).build();

        byte[]  data    = login.toByteArray();
        ByteBuf byteBuf = Unpooled.buffer(data.length + 3);
        byteBuf.writeShort(data.length + 1); // LENGTH
        //byteBuf.writeShort(4); // LENGTH
        byteBuf.writeByte(0); // OP
        //byteBuf.writeBytes(new byte[]{1, 2, 3});
        byteBuf.writeBytes(data);
        channelFuture.channel().writeAndFlush(byteBuf);
    }
}
