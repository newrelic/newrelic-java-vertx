package com.nr.instrumentation.vertx;

public class VertxUtils {

	
	public static boolean tempAddress(String address) {
		for(int i=0;i<address.length();i++) {
			char c = address.charAt(i);
			if(!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	
}
