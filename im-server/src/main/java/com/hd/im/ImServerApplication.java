package com.hd.im;

import com.hd.im.codec.IMServerFrameDecoder;
import com.hd.im.codec.IMServerFrameEncoder;
import com.hd.im.codec.IMServerProtocolDecoder;
import com.hd.im.handler.IMServerProtocolErrorHandler;
import com.hd.im.handler.IMServerLoginHandler;
import com.hd.im.handler.IMServerPublishHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImServerApplication {

    private static Logger logger = LoggerFactory.getLogger(ImServerApplication.class);

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ImServerApplication.class, args);

        NioEventLoopGroup boosGroup   = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    logger.info("client has connect.");
                }

                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();

                    //pipeline.addLast("idleCheck", new IMServerIdleHandler());

                    pipeline.addLast("IMServerFrameDecoder", new IMServerFrameDecoder());
                    pipeline.addLast("IMServerFrameEncoder", new IMServerFrameEncoder());
                    pipeline.addLast(new IMServerProtocolDecoder());

                    pipeline.addLast("decoderErrorHandler", new IMServerProtocolErrorHandler());
                    pipeline.addLast(new IMServerLoginHandler());
                    pipeline.addLast(new IMServerPublishHandler());
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(2020).sync();
            channelFuture.addListener((e) -> {
                if (e.isSuccess()) {
                    logger.info("im server is bind in {}", 2020);
                }
            });
            channelFuture.channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}