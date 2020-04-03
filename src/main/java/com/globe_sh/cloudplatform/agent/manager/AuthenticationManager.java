package com.globe_sh.cloudplatform.agent.manager;

import com.globe_sh.cloudplatform.agent.util.StaticMethod;
import com.globe_sh.cloudplatform.common.bean.DataPackage;
import com.globe_sh.cloudplatform.common.cache.JedisOperater;

import io.netty.channel.socket.SocketChannel;

public class AuthenticationManager {

	//private static Logger logger = LogManager.getLogger(AuthenticationManager.class);
	private static AuthenticationManager instance;

	public static synchronized AuthenticationManager getInstance() {
		if (instance == null) {
			instance = new AuthenticationManager();
		}

		return instance;
	}

	private AuthenticationManager() {

	}
	
	/***
	 * 如果连接id已存在，说明该连接id已登入，认证成功。
	 * 如果连接id不存在，则需判断该次数据是否是登入帧，若是则认证成功，若不是则认证失败。
	 * 
	 * */
	public boolean auth(DataPackage dataPackage) {
		if(!dataPackage.isValid())
			return false;
		byte[] originalData = dataPackage.getSourceData();
		String clientId = dataPackage.getClientId();
		String vin = StaticMethod.ascii2String(originalData, StaticMethod.CLIENT_ID_START, StaticMethod.CLIENT_ID_LENGTH);
		boolean validVin = JedisOperater.validableStation(vin);
		if(!validVin)
			return false;
		/*
		if(NecConnectionManager.getInstance().hasLogin(clientId)) {
			logger.info("Auth Vin<" + vin + "> Success...");
			return true;
		}	*/
		if(isLoginFrame(originalData)) {
			SocketChannel socketChannel = dataPackage.getSocketChannel();
			NecConnectionManager.getInstance().add(clientId, socketChannel);
			return true;
		}

		return false;
	}
	
	private boolean isLoginFrame(byte[] data) {		
		if(data[2] == 0x01) 
			return true;
		
		return false;
	}
}
