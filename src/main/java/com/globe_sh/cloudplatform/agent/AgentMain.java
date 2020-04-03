package com.globe_sh.cloudplatform.agent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.agent.comm.NodeApplication;
import com.globe_sh.cloudplatform.agent.manager.SpringManager;
import com.globe_sh.cloudplatform.agent.net.NecTcpAgent;

import net.sf.json.JSONObject;

public class AgentMain {

	private static Logger logger = LogManager.getLogger(AgentMain.class);
	
	public void init() {
		/*String nodeIp = PropertiesUtil.getInstance().getProperty("NODE_IP");
		if(StringUtils.isEmpty(nodeIp)) {
			logger.info("Faint, Agent节点地址为空，程序退出......");
			System.exit(0);
		}
		StaticVariable.LOCAL_IP = nodeIp;
		logger.info("Agent<" + nodeIp + ">初始化完毕");*/
		SpringManager.registerSpring();
	}
	
	public void run() {
		NecTcpAgent server = new NecTcpAgent();
		try {
			server.runTcp();
			logger.info("Agent启动完毕......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void register() {
		JSONObject json = new JSONObject();
		json.put("node_ip", "127.0.0.1");
		json.put("node_timestamp", System.currentTimeMillis());
		json.put("group", "cluster");
		json.put("actor", "register");
		json.put("param", new JSONObject().put("node_connection", 0));
		NodeApplication.getInstance().run();
		NodeApplication.getInstance().sendMessage(json.toString());
		System.out.println("发送消息: " + json.toString());
	}
	
	public static void main(String args[]) {
		AgentMain agent = new AgentMain();
		agent.init();
		//agent.register();
		agent.run();
	}
}
