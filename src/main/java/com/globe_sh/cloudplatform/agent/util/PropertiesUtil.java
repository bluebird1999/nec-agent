package com.globe_sh.cloudplatform.agent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public class PropertiesUtil {

private static Logger logger = Logger.getLogger(PropertiesUtil.class);
	
	private static PropertiesUtil instance;
	private Properties prop;
	
	private PropertiesUtil() {
		init();
	}
	
	public static synchronized PropertiesUtil getInstance() {
		if(instance == null)
			instance = new PropertiesUtil();
		
		return instance;
	}
	
	public String getProperty(String propertyName) {
		
		return prop.getProperty(propertyName);
	}
	
	private void init() {   
        prop = new Properties(); 
        String path = System.getenv("NECAGENT_HOME"); 
        try {
            if(path == null) {
            	prop.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("config/conf.properties"));
            } else {
	            String propFilePath = path + "/conf.properties"; 
	            InputStream in = new FileInputStream(new File(propFilePath));  
	            prop.load(in); 
	            in.close(); 
            }
        } catch (Exception e) {   
        	logger.error(e.fillInStackTrace());
        }   
    }   
}
