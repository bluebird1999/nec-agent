package com.globe_sh.cloudplatform.agent.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.agent.manager.NecConnectionManager;
import com.globe_sh.cloudplatform.common.bean.DataPackage;
import com.globe_sh.cloudplatform.common.util.StaticMethod;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;


public class SouthMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(SouthMessageProcess.class);
	
	public void process(Exchange message) {
		logger.info("Process Message To Module. ###start###");
		byte[] exchange = (byte[])message.getIn().getBody();
		try {
			DataPackage dataPackage = new DataPackage(exchange);
			logger.info(StaticMethod.bytesToHexString(exchange));
			dataPackage.unpack();
			if(dataPackage.isValid()) {
				String clientId = dataPackage.getClientId();
				byte[] data = dataPackage.getSourceData();
				byte[] appendedData = new byte[ data.length + 2 ];
				System.arraycopy(data, 0, appendedData, 0, data.length);
				appendedData[data.length] = 0x23;
				appendedData[data.length + 1 ] = 0x23;					
				logger.info("To Device Data: " + StaticMethod.bytesToHexString(appendedData));
				SocketChannel channel = NecConnectionManager.getInstance().getChannel(clientId);
				if(channel != null) {
					logger.info("Send By Socket Channel: " + clientId);				
					channel.writeAndFlush(Unpooled.copiedBuffer(appendedData));
				} else {
					logger.info("Cannot Find The Client Socket Channel: " + clientId);
				}
			} else {
				logger.warn("The Data Package Is Invalid...");
			}
		} catch(Exception jmse) {
			logger.error("Process Message Found Exception: ",jmse);
		}
		
		logger.info("Process Message To Module. ###end###");
	}
}
