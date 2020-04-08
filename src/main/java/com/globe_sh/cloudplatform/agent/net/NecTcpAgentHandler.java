package com.globe_sh.cloudplatform.agent.net;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.agent.manager.NecConnectionManager;
import com.globe_sh.cloudplatform.agent.mq.ActivemqOperater;
import com.globe_sh.cloudplatform.agent.util.StaticMethod;
import com.globe_sh.cloudplatform.common.bean.DataPackage;
import com.globe_sh.cloudplatform.common.util.CRC8;
import com.globe_sh.cloudplatform.common.util.StaticOperater;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class NecTcpAgentHandler extends SimpleChannelInboundHandler<Object> {

	private static Logger logger = LogManager.getLogger(NecTcpAgentHandler.class);
	
	/**
	 * 20190402周博要求取消agent鉴权部分，所有的鉴权部分均由server完成
	 * */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("Receive Data Package......"); 
		ByteBuf result = (ByteBuf)msg;
		byte[] data = new byte[result.readableBytes()];
		result.readBytes(data);
		if( data.length <=0 ) {
			logger.info("Find empty message, return...");
			return;
		}
		logger.info("Receive Data Content: " + StaticMethod.bytesToHexString(data));
		byte[] newdata = new byte[data.length + 2];
		newdata[0] = 0x23;
		newdata[1] = 0x23;
		System.arraycopy(data, 0, newdata, 2, data.length);
		
		String clientId = ctx.channel().id().asLongText();
		DataPackage dataPackage = new DataPackage(clientId, newdata, (SocketChannel)ctx.channel());
		dataPackage.pack();
		if(dataPackage.isValid()) {
			ActivemqOperater.getInstance().sendMessageDefault(dataPackage.getTargetData());
			logger.info("Authentication Success, Client ID: " + clientId);
		} else {
			String hexString = StaticMethod.bytesToHexString(data);
			logger.warn("Client ID: " + clientId + "\tInvalid Data: " + hexString);
		}
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("channelRead0......");
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String clientId = ctx.channel().id().asLongText();
		logger.info("channelActive,Client ID: " + clientId);
		if(NecConnectionManager.getInstance().getCurrentConnection() < 300) {
			NecConnectionManager.getInstance().add(clientId, (SocketChannel)ctx.channel());
			ctx.channel().writeAndFlush(Unpooled.copiedBuffer("success".getBytes()));  //73 75 63 63 65 73 73
		} else {
			String ip = StaticOperater.getIdleAgent();
			ctx.channel().writeAndFlush(Unpooled.copiedBuffer(ip.getBytes()));
			ctx.close();
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		byte[] data = new byte[26];
		data[0] = 0x23;
		data[1] = 0x23;
		data[2] = 0x40;//通知server关闭连接
		data[23] = 0x01;
		data[24] = 0x7F;//随便设一个数据
		byte crc8 = CRC8.calcCrc8WithoutPrefix(data);
		data[25] = crc8;
		String clientId = ctx.channel().id().asLongText();
		logger.info("Channel InActive,Client ID: " + clientId);
		DataPackage dataPackage = new DataPackage(clientId, data, (SocketChannel)ctx.channel());
		dataPackage.pack();
		if(dataPackage.isValid()) {
			logger.info("Data Length: " + dataPackage.getTargetData().length + "\tClient ID: " + clientId);
			ActivemqOperater.getInstance().sendMessageDefault(dataPackage.getTargetData());
		} else {
			logger.info("Data Error When Channel Inactive...");
		}
		
		NecConnectionManager.getInstance().remove(clientId);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		String clientId = ctx.channel().id().asLongText();
		logger.info("Cause: " + cause.getMessage());
		logger.info("Channel Exception Caught,Client ID: " + clientId);
		NecConnectionManager.getInstance().remove((SocketChannel)ctx.channel());
		ctx.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}