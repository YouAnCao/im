package com.hd.im.service;

import com.google.protobuf.ByteString;
import com.im.core.utils.RSAUtils;
import com.im.core.proto.HDIMProtocol;
import com.im.core.utils.AESHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

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
        String                  pk            = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmC8TYMczQDCrlxCkb5JJ1pxFiHStuo7skhtMm5aKMTtyI+FdFTFVF6gTgt01AbKEdFafO8htyXABkhPRKo4kD1J+r1z15ueFmFOXvjoWJUjVh2Z4U8m85qOlSwSxKBr0qDpyTuvU82T/iT8hFU4UvDydrkZu4IXS4m2uJJ0fuswIDAQAB";
        String                  token         = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ5aGpjIiwiZXhwIjoxNjIwNzAzMzYzLCJhdWQiOiJ1c2VyIiwianRpIjoiMTIzNzU4MDkwNjc5OTkyMzIwMiIsInVzZXJuYW1lIjoiaGRfYTdmNTM3ZWZmYTFiYTY2MGI5NTc3ZTI3ZDIzZjJhY2MiLCJyZWFsTmFtZSI6IjEyMyIsInBsYXRmb3JtIjoiQU5EUk9JRCIsImlwIjoiMTkyLjE2OC4wLjE0MSJ9.1V1mjKM6FEPTmZJjzHIE_G0CT3xJXJR755_8AERGUpY";
        ChannelFuture           channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 2020)).sync();
        HDIMProtocol.ClientInfo android       = HDIMProtocol.ClientInfo.newBuilder().setClientId("0123456789").setUserId("1237580906799923202").setPlatForm(HDIMProtocol.PlatForm.ANDROID.getNumber()).setAppVersion("1.0.0").build();
        String                  password      = "c8a9229820ffa315bc6a17a9e43d01a9";
        byte[]                  encrypt       = AESHelper.encrypt(android.toByteArray(), password);
        //byte[]                  encrypt    = AESUtils.encrypt(android.toByteArray(), "123321");
        String encryptKey = null;
        try {
            encryptKey = RSAUtils.encryptByPubKey(password, pk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" 加密AES密钥： " + encryptKey);
        // String decryptByPriKey = RSAUtils.decryptByPriKey(encryptKey, "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKKc7LrV/+d/SWgpOn151se9HkPZCmAR1m01h3dO3NK7KrdIMQ45Tjo6MROHzC1jpEEPD2z4rZ5eLBSGUfSUAIId0d9N/QJsLEpgep7DFrJSPhXax0B0jBw8mJUTGAGHU6LFsWej7tziipOFukq7YsEEB32SY4eKXo9L0JUiuohBAgMBAAECgYApopxKSji3gTO+Y7ACKIwBoD57iLrLVkXcuHPy7FJF8n5BZ06IIcwRxIyEP6TbikOoFL8SW6m4fswyhSG+yZZHUIC5ysDhnycpSCF3Cd7OElZNNA20TrydMV72Um6uVKXAiLAH+aNdqpGnxQ6ZelupIdKwEHk3MeUuUx4fk6NCQQJBAPExNcMtkyfsc0OfHK0RjmFMN6+oYE4mRO5r4eM7jAj4OWe9DHkBGD+uThq97eYP0ekLydOLk5lyuL4ZCoQQrc0CQQCsmLGqn+PyKKTmYZfCkhvt8umLUA5mdcUHUnqoEK9LZmDJSfbRK8e4C+ViWFmF+Vtm9k2aNYJjF+USrwSU+nBFAkBUSALmaiWS/OAmUBJgM9NSEqUe37KPfHX37oDnu6YqeDrvEjOqkQRdNFacp6PbEcojKnhjUNHJ/DmsS+nNzlo9AkBAuQHm+g6AwL0Vp4NBc4kFshHiLCM6SO+Zz1QvshIKNFoXIaVsyv4aBlv+hSMNGnUsJzDyox5CCrPcTcVqGIABAkEAmyj1un8hXRnZtPpytuFMSxXInzvztcjJbsleOMqUown/a+eqaTNwb3ZgiPJJtCRNUPMax77+iK+rNPkS4wFTiQ==");
        // System.out.println(" 解密后AES密钥:" + decryptByPriKey);
        HDIMProtocol.Login login = HDIMProtocol.Login.newBuilder().setToken(token).
                setEncryptData(ByteString.copyFrom(encrypt)).
                setEncryptKey(ByteString.copyFrom(encryptKey, "utf-8")).build();

        byte[]  data    = login.toByteArray();
        ByteBuf byteBuf = Unpooled.buffer(data.length + 3);
        byteBuf.writeShort(data.length + 1); // LENGTH
        //byteBuf.writeShort(4); // LENGTH
        byteBuf.writeByte(0); // OP
        //byteBuf.writeBytes(new byte[]{1, 2, 3});
        byteBuf.writeBytes(data);
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(byteBuf);

        HDIMProtocol.PullMessageRequest pullMessageRequest = HDIMProtocol.PullMessageRequest.newBuilder().setMessageHead(1L).build();
        byte[]                          encrypt1           = AESHelper.encrypt(pullMessageRequest.toByteArray(), password);
        byte[]                          decrypt            = AESHelper.decrypt(encrypt1, password.getBytes());
        HDIMProtocol.PullMessageRequest request            = HDIMProtocol.PullMessageRequest.parseFrom(decrypt);
        HDIMProtocol.Publish build = HDIMProtocol.Publish.newBuilder().setPublishType(HDIMProtocol.PublishType.MP_VALUE).
                setSequenceId(System.currentTimeMillis()).
                setPayload(ByteString.copyFrom(encrypt1)).build();
        byte[]  payload = build.toByteArray();
        ByteBuf mpBuf   = Unpooled.buffer(payload.length + 3);
        mpBuf.writeShort(payload.length + 1);
        mpBuf.writeByte(HDIMProtocol.HeadType.PUBLISH_VALUE);
        mpBuf.writeBytes(payload);
        channel.writeAndFlush(mpBuf);


        //        HDIMProtocol.Conversation conversation = HDIMProtocol.Conversation.newBuilder().setTarget("1235141817303592962").
        //                setType(HDIMProtocol.ConversationType.SIGNAL_VALUE).build();
        //        HDIMProtocol.MessageContent messageContent = HDIMProtocol.MessageContent.newBuilder().setContentType(HDIMProtocol.MessageContentType.TEXT_VALUE).setData(ByteString.copyFrom("你好", "utf-8")).build();
        //        HDIMProtocol.Message        message        = HDIMProtocol.Message.newBuilder().setConversation(conversation).setMessageContent(messageContent).setFrom("1237580906799923202").setTo("1235141817303592962").build();
        //
        //        byte[] encryptMsg = AESHelper.encrypt(message.toByteArray(), password);
        //
        //        HDIMProtocol.Publish publish  = HDIMProtocol.Publish.newBuilder().setSequenceId(1L).setPublishType(HDIMProtocol.PublishType.MS_VALUE).setPayload(ByteString.copyFrom(encryptMsg)).build();
        //        byte[]               payload  = publish.toByteArray();
        //        ByteBuf              transfer = Unpooled.buffer(payload.length + 3);
        //
        //
        //        /* 解密 */
        //        HDIMProtocol.Publish publish1 = HDIMProtocol.Publish.parseFrom(payload);
        //        byte[]               encrypt1 = publish1.getPayload().toByteArray();
        //        String               decrypt1 = AESHelper.decrypt(encrypt1, password);
        //        HDIMProtocol.Message message1 = HDIMProtocol.Message.parseFrom(decrypt1.getBytes("utf-8"));
        //        System.out.println(new String(message1.getMessageContent().getData().toByteArray()));
        //
        //        transfer.writeShort(payload.length + 1);
        //        transfer.writeByte(HDIMProtocol.HeadType.PUBLISH_VALUE);
        //        transfer.writeBytes(payload);
        //


    }
}
