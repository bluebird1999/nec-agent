package com.globe_sh.cloudplatform.agent.util;

public class ProtocolUtil {

	public static String getVin(byte[] data) {
		if(data == null || data.length < 22)
			return null;
		
		String vin = "";
		for(int i = 4; i < 22; i++) {
			byte b = data[i];
			char c = (char)b;
			vin = vin + c;
		}
		
		return vin;
	}
	
}
