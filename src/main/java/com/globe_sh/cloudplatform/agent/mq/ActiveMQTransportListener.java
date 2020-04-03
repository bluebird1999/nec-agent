package com.globe_sh.cloudplatform.agent.mq;

import java.io.IOException;

import org.apache.activemq.transport.TransportListener;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.agent.mq.ActiveMQTransportListener;

public class ActiveMQTransportListener implements TransportListener {

	private Logger log = Logger.getLogger(ActiveMQTransportListener.class);

	/**
	 * 对消息传输命令进行监控
	 * 
	 * @param command
	 */
	public void onCommand(Object o) {

	}

	/**
	 * 对监控到的异常进行触发
	 * 
	 * @param error
	 */
	public void onException(IOException error) {
		log.error("onException -> 消息服务器连接错误......", error);
	}

	/**
	 * 当failover时触发
	 */
	public void transportInterupted() {
		log.warn("transportInterupted -> 消息服务器连接发生中断...");
	}

	/**
	 * 监控到failover恢复后进行触发
	 */
	public void transportResumed() {
		log.info("transportResumed -> 消息服务器连接已恢复...");
	}
}
