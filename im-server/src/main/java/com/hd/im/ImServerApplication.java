package com.hd.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hd.im.codec.IMServerProtocolDecoder;
import com.hd.im.codec.IMServerProtocolDecryptDecoder;
import com.hd.im.codec.IMServerFrameDecoder;
import com.hd.im.handler.IMServerLoginHandler;
import com.hd.im.handler.IMServerPublishHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

@SpringBootApplication
public class ImServerApplication {

	private static Logger logger = LoggerFactory.getLogger(ImServerApplication.class);

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(ImServerApplication.class, args);

		NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
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
					pipeline.addLast(new IMServerFrameDecoder());
					pipeline.addLast(new IMServerProtocolDecryptDecoder());
					pipeline.addLast(new IMServerProtocolDecoder());

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
