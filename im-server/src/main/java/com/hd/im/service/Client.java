package com.hd.im.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Base64;

import com.google.protobuf.ByteString;
import com.hd.im.proto.HDIMProtocol;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap         bootstrap   = new Bootstrap();
        bootstrap.group(workerGroup);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
            }
        });

        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 2020)).sync();

        HDIMProtocol.Login login = HDIMProtocol.Login.newBuilder().setToken("token")
                .setEncryptData(ByteString.copyFrom("encrypt".getBytes()))
                .setEncryptKey(ByteString.copyFrom("key".getBytes())).build();
        byte[]  data    = login.toByteArray();
        ByteBuf byteBuf = Unpooled.buffer(data.length + 3);
        byteBuf.writeShort(data.length); // LENGTH
        byteBuf.writeByte(0); // OP

        byteBuf.writeBytes(data);
        channelFuture.channel().writeAndFlush(byteBuf);
        try {
            Thread.sleep(1000 * 16);
        } catch (Exception e) {

        }
        channelFuture.channel().writeAndFlush(byteBuf);
    }
}
