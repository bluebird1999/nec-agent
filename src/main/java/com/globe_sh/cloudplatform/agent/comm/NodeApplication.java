package com.globe_sh.cloudplatform.agent.comm;


import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

public class NodeApplication {

	private static NodeApplication instance;
	private ActorSelection selection;

	public static synchronized NodeApplication getInstance() {
		if (instance == null) {
			instance = new NodeApplication();
		}

		return instance;
	}

	private NodeApplication() {
		
	}
	
	public void run() {
		ActorSystem system = ActorSystem.create("node", ConfigFactory.load("application.conf"));
		selection = system.actorSelection("akka.tcp://master@127.0.0.1:30004/user/MasterActor");		
	}
	
	public void sendMessage(String message) {
		selection.tell(message, selection.anchor());
	}
}
