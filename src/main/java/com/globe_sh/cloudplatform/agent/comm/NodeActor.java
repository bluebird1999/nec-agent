package com.globe_sh.cloudplatform.agent.comm;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import akka.actor.UntypedActor;

public class NodeActor extends UntypedActor  {

	private static Logger logger = LogManager.getLogger(NodeActor.class);
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			dealMessage((String)message);
        } else {
        	dealMessageTypeError(message);
        }
	}
	
	private void dealMessage(String message) {
		System.out.println("收到消息: " + message);
	}
	
	private void dealMessageTypeError(Object msg) {
		logger.info("消息类型不匹配......");
	}
	
}
