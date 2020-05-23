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
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap         bootstrap   = new Bootstrap();
        bootstrap.group(workerGroup);
        String password = "c8a9229820ffa315bc6a17a9e43d01a9";
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {

            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldPrepender(4));
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("active");
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg != null) {
                            ByteBuf data  = (ByteBuf) msg;
                            int     len   = data.readInt();
                            byte[]  trans = new byte[len];
                            data.readBytes(trans);
                            HDIMProtocol.MessagePack messagePack = HDIMProtocol.MessagePack.parseFrom(trans);
                            int                      command     = messagePack.getCommand();
                            HDIMProtocol.IMCommand   command1    = HDIMProtocol.IMCommand.forNumber(command);
                            System.out.println(command1);
                            byte[] decrypt = AESHelper.decrypt(messagePack.getPayload().toByteArray(), password.getBytes());
                            if (command1 == HDIMProtocol.IMCommand.LOGIN) {
                                HDIMProtocol.LoginResponse loginResponse = HDIMProtocol.LoginResponse.parseFrom(decrypt);
                                System.out.println(loginResponse);
                            }
                        }
                    }

                });
            }
        });
        String addr      = "192.168.0.126";
        int    port      = 2020;
        String rasPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJjeOCquJuUsREGdOuX4FAq7YZFhZZ3lKZlsW5oGOKCQG2yyuPoaupUARKM06lfEInHYYibkdeJH5/xDqVvAjMmRYP2e7CH1rd8/zAzzDzMvGOwDouVsSYNiBXuM5H1ABIZm5/o0LxpS/6tffLaQuDezpy3sG9zZNmmDUhtpSqSwIDAQAB";
        String token     = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ5aGpjIiwiZXhwIjoxNjIwNzAzMzYzLCJhdWQiOiJ1c2VyIiwianRpIjoiMTIzNzU4MDkwNjc5OTkyMzIwMiIsInVzZXJuYW1lIjoiaGRfYTdmNTM3ZWZmYTFiYTY2MGI5NTc3ZTI3ZDIzZjJhY2MiLCJyZWFsTmFtZSI6IjEyMyIsInBsYXRmb3JtIjoiQU5EUk9JRCIsImlwIjoiMTkyLjE2OC4wLjE0MSJ9.1V1mjKM6FEPTmZJjzHIE_G0CT3xJXJR755_8AERGUpY";

        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(addr, port)).sync();
        channelFuture.addListener(e -> {
            if (e.isSuccess()) {
                /* 登录 */
                Channel channel = channelFuture.channel();
                try {
                    HDIMProtocol.ClientInfo clientInfo = HDIMProtocol.ClientInfo.newBuilder()
                            .setClientId("0123456789")
                            .setUserId("1237580906799923202")
                            .setPlatForm(HDIMProtocol.PlatForm.ANDROID.getNumber())
                            .setAppVersion("1.0.0").build();
                    byte[] encrypt    = AESHelper.encrypt(clientInfo.toByteArray(), password);
                    String encryptKey = encryptKey = RSAUtils.encryptByPubKey(password, rasPubKey);
                    HDIMProtocol.Login login = HDIMProtocol.Login.newBuilder().setToken(token).
                            setEncryptData(ByteString.copyFrom(encrypt)).
                            setEncryptKey(ByteString.copyFrom(encryptKey, "utf-8")).build();
                    byte[]                           data    = login.toByteArray();
                    HDIMProtocol.MessagePack.Builder builder = HDIMProtocol.MessagePack.newBuilder();
                    builder.setPayload(ByteString.copyFrom(data));
                    builder.setCommand(HDIMProtocol.IMCommand.LOGIN_VALUE);
                    builder.setSequenceId(System.currentTimeMillis());
                    HDIMProtocol.MessagePack msgPack           = builder.build();
                    byte[]                   loginTransferData = msgPack.toByteArray();
                    ByteBuf                  byteBuf           = Unpooled.buffer(loginTransferData.length);
                    byteBuf.writeBytes(loginTransferData);
                    channel.writeAndFlush(byteBuf);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                /* 拉取好友列表 */
                try {
                    HDIMProtocol.GetFriendsRequest build   = HDIMProtocol.GetFriendsRequest.newBuilder().build();
                    byte[]                         data    = build.toByteArray();
                    byte[]                         encrypt = AESHelper.encrypt(data, password);

                    HDIMProtocol.MessagePack msgPack = HDIMProtocol.MessagePack.newBuilder().setCommand(HDIMProtocol.IMCommand.FP_VALUE)
                            .setSequenceId(System.currentTimeMillis())
                            .setPayload(ByteString.copyFrom(encrypt)).build();
                    byte[]  bytes  = msgPack.toByteArray();
                    ByteBuf buffer = channel.alloc().buffer(bytes.length);
                    buffer.writeBytes(bytes);
                    channel.writeAndFlush(buffer);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });


        //        HDIMProtocol.PullMessageRequest pullMessageRequest = HDIMProtocol.PullMessageRequest.newBuilder().setMessageHead(1L).build();
        //        byte[]                          encrypt1           = AESHelper.encrypt(pullMessageRequest.toByteArray(), password);
        //        byte[]                          decrypt            = AESHelper.decrypt(encrypt1, password.getBytes());
        //        HDIMProtocol.PullMessageRequest request            = HDIMProtocol.PullMessageRequest.parseFrom(decrypt);
        //        HDIMProtocol.Publish build = HDIMProtocol.Publish.newBuilder().setPublishType(HDIMProtocol.PublishType.MP_VALUE).
        //                setSequenceId(System.currentTimeMillis()).
        //                setPayload(ByteString.copyFrom(encrypt1)).build();
        //        byte[]  payload = build.toByteArray();
        //        ByteBuf mpBuf   = Unpooled.buffer(payload.length + 3);
        //        mpBuf.writeShort(payload.length + 1);
        //        mpBuf.writeByte(HDIMProtocol.HeadType.PUBLISH_VALUE);
        //        mpBuf.writeBytes(payload);
        //        channel.writeAndFlush(mpBuf);


        //        HDIMProtocol.Conversation conversation = HDIMProtocol.Conversation.newBuilder().setTarget("1235141817303592962").
        //                setType(HDIMProtocol.ConversationType.SIGNAL_VALUE).build();
        //        HDIMProtocol.MessageContent      messageContent = HDIMProtocol.MessageContent.newBuilder().setContentType(HDIMProtocol.MessageContentType.TEXT_VALUE).setData(ByteString.copyFrom("你好", "utf-8")).build();
        //        HDIMProtocol.Message             message        = HDIMProtocol.Message.newBuilder().setConversation(conversation).setMessageContent(messageContent).setFrom("1237580906799923202").setTo("1235141817303592962").build();
        //        byte[]                           encrypt1       = AESHelper.encrypt(message.toByteArray(), password);
        //        HDIMProtocol.MessagePack.Builder builder1       = HDIMProtocol.MessagePack.newBuilder();
        //        builder1.setCommand(HDIMProtocol.IMCommand.MS_VALUE);
        //        builder1.setSequenceId(System.currentTimeMillis());
        //        builder1.setPayload(ByteString.copyFrom(encrypt1));
        //        HDIMProtocol.MessagePack build    = builder1.build();
        //        byte[]                   bytes1   = build.toByteArray();
        //        ByteBuf                  transfer = channel.alloc().buffer();
        //        transfer.writeShort(bytes1.length);
        //        transfer.writeBytes(bytes1);
        //        channel.writeAndFlush(transfer);
        //        //
        //
        //        HDIMProtocol.MessagePack build1 = HDIMProtocol.MessagePack.newBuilder().setCommand(HDIMProtocol.IMCommand.PING_VALUE).build();
        //        ByteBuf                  buffer = channel.alloc().buffer();
        //        byte[]                   bytes2 = build1.toByteArray();
        //        buffer.writeBytes(bytes2);
        //        channel.writeAndFlush(buffer);

        System.in.read();
    }
}
