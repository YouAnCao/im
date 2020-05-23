package com.hd.im;

import com.hd.im.cache.MemorySessionStore;
import com.hd.im.netty.channel.NettyChannelKeys;
import com.hd.im.netty.codec.IMServerFrameDecoder;
import com.hd.im.netty.codec.IMServerFrameEncoder;
import com.hd.im.netty.codec.IMServerProtocolDecoder;
import com.hd.im.netty.codec.IMServerProtocolEncoder;
import com.hd.im.netty.handler.IMServerIdleHandler;
import com.hd.im.netty.handler.IMServerMessagePackHandler;
import com.im.core.entity.UserSession;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
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
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                    if (ctx.channel().attr(NettyChannelKeys.USER_SESSION) != null) {
                        UserSession session = ctx.channel().attr(NettyChannelKeys.USER_SESSION).get();
                        if (session != null) {
                            MemorySessionStore.getInstance().tickClient(session.getClientId());
                        }
                    }
                    if (ctx.channel() != null && ctx.channel().isActive() || ctx.channel().isOpen()) {
                        ctx.close();
                    }
                }

                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();
                    pipeline.addLast("IMServerLogger", new LoggingHandler(LogLevel.INFO));

                    pipeline.addLast("idleCheck", new IMServerIdleHandler());

                    pipeline.addLast("IMServerFrameDecoder", new IMServerFrameDecoder());
                    pipeline.addLast("IMServerFrameEncoder", new IMServerFrameEncoder());
                    pipeline.addLast(new IMServerProtocolDecoder());
                    pipeline.addLast(new IMServerProtocolEncoder());

                    pipeline.addLast(new IMServerMessagePackHandler());
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