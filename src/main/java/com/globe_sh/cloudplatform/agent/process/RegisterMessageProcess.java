package com.globe_sh.cloudplatform.agent.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class RegisterMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(RegisterMessageProcess.class);
	
	public void process(Exchange message) {
		logger.info("Receive Registe Response. ###start###");
		byte[] exchange = (byte[])message.getIn().getBody();
		try {
			String receiveMessage = new String(exchange);
			JSONObject json = JSONObject.fromObject(receiveMessage);
			if("success".equals(json.getString("registerResponse"))) {
				//new NecTcpAgent().runTcp();
				logger.info("Agent successfully register and the service started......");
			}
		} catch(Exception jmse) {
			logger.error("Receive Registe Response Found Exception: ",jmse);
		}
		
		logger.info("Receive Registe Response. ###end###");
	}
}
