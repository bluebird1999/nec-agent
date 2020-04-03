package com.globe_sh.cloudplatform.agent.util;

public class StaticMethod {

	public static final int CLIENT_ID_LENGTH = 17;
	public static final int CLIENT_ID_START = 4;
	public static final int TCP_PORT = 30003;
	
	public static final byte PACK_DATA_INSIDE = 0x25;
	public static final byte PACK_DATA_OUTSIDE = 0x24;
	public static final int PACK_CONNECT_ID_LENGTH = 60;
	
	public static boolean isNull(String s) {
		if(s == null || "".equals(s))
			return true;
		
		return false;
	}
	
	public static boolean isNull(byte[] data) {
		if(data == null || data.length == 0)
			return true;
		
		return false;
	}
	
	public static String ascii2String(byte[] data, int start, int length) {
		String s = "";
		int len = start + length;
		for(int i = start; i < len; i++) {
			byte b = data[i];
			char c = (char)b;
			s = s + c;
		}
		
		return s;
	}
	
	public static byte[] string2Ascii(String str) {
		byte[] data = null;
		try {
			data = str.getBytes("US-ASCII");
		} catch(Exception e) {
			
		}
		
		return data;
	}
	
	public static String bytesToHexString(byte[] src){   
	    StringBuilder stringBuilder = new StringBuilder("");   
	    if (src == null || src.length == 0) {   
	        return null;   
	    }   
	    for (int i = 0; i < src.length; i++) {   
	        int v = src[i] & 0xFF;   
	        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
	            stringBuilder.append(0);   
	        }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString();   
	}   
}
