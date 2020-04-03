package com.globe_sh.cloudplatform.agent.manager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringManager {

	private static SpringManager instance;
	
	private ApplicationContext ac = null;
	
	private SpringManager() {
		ac = new ClassPathXmlApplicationContext(new String[] 
				{"config/spring-config.xml"});
	}
	
	public static synchronized SpringManager registerSpring() {
		if(instance == null)
			instance = new SpringManager();
		
		return instance;
	}
	
	public ApplicationContext getApplicationContext() {
		
		return ac;
	}
	
}

