package com.globe_sh.cloudplatform.agent.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.agent.manager.NecConnectionManager;


public class CloseMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(CloseMessageProcess.class);
	
	public void process(Exchange message) {
		logger.info("Close Module Connection. ###start###");
		byte[] exchange = (byte[])message.getIn().getBody();
		try {
			String clientId = new String(exchange);
			logger.warn("Close Socket Because Overtime For Client: " + clientId);
			NecConnectionManager.getInstance().closeSocket(clientId);			
		} catch(Exception jmse) {
			logger.error("Close Module Connection Found Exception: ",jmse);
		}
		
		logger.info("Close Module Connection. ###end###");
	}
}
