package com.globe_sh.cloudplatform.agent.net;

import java.net.InetSocketAddress;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.agent.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticOperater;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

public class NecTcpAgent {

	private static Logger logger = LogManager.getLogger(NecTcpAgent.class);
	
	public static String localIp = StaticOperater.getLocalIp();
	
	public void runTcp() throws Exception {
		new Thread(new Runnable() {
			public void run() {
				logger.info("Nec Tcp Agent Running...");
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
					ServerBootstrap b = new ServerBootstrap();
					b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
							.localAddress(new InetSocketAddress(StaticMethod.TCP_PORT))
							.childHandler(new ChannelInitializer<SocketChannel>() {
								/**
								 * DelimiterBasedFrameDecoder     特殊分割符方式处理拆包粘包问题
								 * LineBasedFrameDecoder          以行的方式处理拆包粘包问题
								 * */
								@Override
								public void initChannel(SocketChannel ch) throws Exception {
									ByteBuf delimiter = Unpooled.copiedBuffer("##".getBytes());
									ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1500,delimiter));
									//一包数据的最大长度不会超过1500,若超过则需调整此参数
									ch.pipeline().addLast(new NecTcpAgentHandler());
									//ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
								}
							})
							.option(ChannelOption.SO_BACKLOG, 1024)
							.option(ChannelOption.TCP_NODELAY, true)
							.childOption(ChannelOption.SO_KEEPALIVE, true);
					ChannelFuture f = b.bind().sync();
					f.channel().closeFuture().sync();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}
		}).start();
	}
}
