package com.hd.im.service;

import com.google.protobuf.ByteString;
import com.hd.im.commons.proto.HDIMProtocol;
import com.hd.im.commons.utils.AESHelper;
import com.hd.im.commons.utils.RSAUtils;
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
        String                  pk            = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCylWRQDAWpw6hJNpqy5RNNwrHjYbsNOSAzcMm1eOhxqB2cJq/Tkr2FYQlVhMdh/ZBdUhIYB5jCUlzMN706ra0ZBczz4Zv03rZL8+z64fAI347NvYheKj7ID7FKoYHzxAqquXdh1uuN7PO1zN53KHBY8jqESEwUQ/DMFzV60X+zZQIDAQAB";
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
        String decryptByPriKey = RSAUtils.decryptByPriKey(encryptKey, "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALKVZFAMBanDqEk2mrLlE03CseNhuw05IDNwybV46HGoHZwmr9OSvYVhCVWEx2H9kF1SEhgHmMJSXMw3vTqtrRkFzPPhm/Tetkvz7Prh8Ajfjs29iF4qPsgPsUqhgfPECqq5d2HW643s87XM3ncocFjyOoRITBRD8MwXNXrRf7NlAgMBAAECgYEAn4iZrnCu9nNVeOIQ3NOLpSHSrGB4K13nCE0vxLAYZsBag06EMCucpvSC1CTjwQQ13Ugd5d2Td7UANWdt4meYg42JaWNnYgpRUtuFvmQLab0l91g5HexjJOL2ea2XFO3SlNp9ZRYmE3pbxH7lyiHKK6E/+RuN9STwdTLq22/rmEECQQDg84lQckPOgRvbKahWf4eyCckR5myheMne9CsnLZscEKYWrOaQVZpsOY/jdZNCBCh8RYlTDNfL3oOyIg5apaBZAkEAyzt9hfB3EOkAthHxVVMc28Ur22xlR6tFRXoAEsYGD90csH5h1DpGuIiwng8vg3FDm09IRKY54GL5hiiwy9wp7QJAUpWWB86rThjgHSnB/196dcZZ5xRtNSqfEhcZ8JwxobALCbAYRUzoEGthg+QaV9Ym9tigfbjtUI80UiVml6jOoQJBAMRzTxTHqvCHqo5Urcniu0SdRZWnJAexQRa1pZiAHp/7Bp58rCgv5OjACKng1/bg548bhioF3HBQfA75Cl0lBskCQDEKrbMxixNQyrOidRWaiirefkoBslMsQ712zRbDH05icwV5pDpRJcBtoe5axOFdY54QKUzP2KxUxZHl7W7bAhU=");
        System.out.println(" 解密后AES密钥:" + decryptByPriKey);
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

        HDIMProtocol.Conversation   conversation   = HDIMProtocol.Conversation.newBuilder().setTarget("hello world").setType(HDIMProtocol.ConversationType.SIGNAL_VALUE).build();
        HDIMProtocol.MessageContent messageContent = HDIMProtocol.MessageContent.newBuilder().setContentType(HDIMProtocol.MessageContentType.TEXT_VALUE).setData(ByteString.copyFrom("你好", "utf-8")).build();
        HDIMProtocol.Message        message        = HDIMProtocol.Message.newBuilder().setConversation(conversation).setMessageContent(messageContent).setFrom("1237580906799923202").setTo("1237580906799923202").build();
        HDIMProtocol.Publish        publish        = HDIMProtocol.Publish.newBuilder().setPublishType(HDIMProtocol.PublishType.MS_VALUE).setPayload(ByteString.copyFrom(message.toByteArray())).build();
        byte[]                      payload       = AESHelper.encrypt(publish.toByteArray(), password);
        ByteBuf                     transfer       = Unpooled.buffer(payload.length + 3);
        transfer.writeShort(payload.length + 1);
        transfer.writeByte(HDIMProtocol.HeadType.PUBLISH_VALUE);
        transfer.writeBytes(payload);
        channel.writeAndFlush(transfer);
    }
}
