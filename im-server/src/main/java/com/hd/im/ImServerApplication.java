package com.hd.im;

import com.hd.im.netty.codec.IMServerFrameDecoder;
import com.hd.im.netty.codec.IMServerFrameEncoder;
import com.hd.im.netty.codec.IMServerProtocolDecoder;
import com.hd.im.netty.codec.IMServerProtocolEncoder;
import com.hd.im.netty.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
public class ImServerApplication {

    private static Logger logger = LoggerFactory.getLogger(ImServerApplication.class);

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ImServerApplication.class, args);

        final ByteBuf     limiter     = Unpooled.copiedBuffer("\r\n".getBytes());
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
                    ctx.close();
                    logger.error("", cause);
                }

                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();

                    pipeline.addLast("idleCheck", new IMServerIdleHandler());
                    pipeline.addLast("IMServerLogger", new LoggingHandler(LogLevel.INFO));
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