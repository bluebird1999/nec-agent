package com.globe_sh.cloudplatform.agent.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.agent.net.NecTcpAgent;
import com.globe_sh.cloudplatform.common.cache.JedisOperater;
import com.globe_sh.cloudplatform.common.util.StaticOperater;
import com.globe_sh.cloudplatform.common.util.StaticVariable;

import io.netty.channel.socket.SocketChannel;

public class NecConnectionManager {

	private static Logger logger = LogManager.getLogger(NecConnectionManager.class);
	private static NecConnectionManager instance;
	private Map<String, SocketChannel> connectionMap = new ConcurrentHashMap<String, SocketChannel>();

	public static synchronized NecConnectionManager getInstance() {
		if (instance == null) {
			instance = new NecConnectionManager();
		}

		return instance;
	}

	private NecConnectionManager() {

	}
	
	public int getCurrentConnection() {
		
		return connectionMap.size();
	}
	
	public void add(String clientId, SocketChannel socketChannel) {
		connectionMap.put(clientId, socketChannel);
		updateConnection();
	}
	
	public void closeSocket(String clientId) {
		SocketChannel socketChannel = connectionMap.get(clientId);
		if(socketChannel != null) {
			try {
				socketChannel.close();
				connectionMap.remove(clientId);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else
			logger.info("socket is already closed!");
		updateConnection();
	}
	
	private void updateConnection() {

		int num = connectionMap.size();
		String ip = StaticVariable.LOCAL_IP;
		JedisOperater.updateAgentConnection(ip, num);
	}

	public SocketChannel getChannel(String clientId) {
		return connectionMap.get(clientId);
	}
	
	public void remove(String clientId) {
		connectionMap.remove(clientId);
	}
	
	@SuppressWarnings("rawtypes")
	public void remove(SocketChannel socketChannel) {
		for ( Map.Entry entry : connectionMap.entrySet()) {
			if (entry.getValue() == socketChannel) {
				logger.info("Remove Connect Client ID: " + entry.getKey());
				connectionMap.remove(entry.getKey());
			}
		}
	}
}
