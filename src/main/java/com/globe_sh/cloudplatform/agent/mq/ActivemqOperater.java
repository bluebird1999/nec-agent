package com.globe_sh.cloudplatform.agent.mq;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.globe_sh.cloudplatform.agent.manager.SpringManager;

public class ActivemqOperater {

private static Logger logger = Logger.getLogger(ActivemqOperater.class);
	
	private static ActivemqOperater instance;
	
	private JmsTemplate jmsTemplate;
	
	private final ActiveMQQueue clusterQueue = new ActiveMQQueue("agent-cluster");
	
	private ActivemqOperater() {
		jmsTemplate = (JmsTemplate)SpringManager.registerSpring().getApplicationContext().getBean("jmsTemplate");
	}
	
	public static synchronized ActivemqOperater getInstance() {
		if(instance == null)
			instance = new ActivemqOperater();
		
		return instance;
	}
	
	public void sendMessageDefault(final byte[] data) {
		try {
			jmsTemplate.send(new MessageCreator() {
	            public Message createMessage(Session session) throws JMSException {
	            	BytesMessage bytesMessage = session.createBytesMessage();
	            	bytesMessage.writeBytes(data);
	            	
	                return bytesMessage;
	            }
	        });
		} catch (Exception e) {
			logger.error("", e);
	    }
	}
	
	public void sendMessageNodeRegister(final byte[] data) {
		try {
			jmsTemplate.send(clusterQueue, new MessageCreator() {
	            public Message createMessage(Session session) throws JMSException {
	            	BytesMessage bytesMessage = session.createBytesMessage();
	            	bytesMessage.writeBytes(data);
	            	
	                return bytesMessage;
	            }
	        });
		} catch (Exception e) {
			logger.error("", e);
	    }
	}
}
