package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import io.vertx.core.impl.future.Listener;

public class VertxUtils {

	private static final String REPLY = "vertx.reply";
	private static final String REGEX = "([a-z]|[0-9]){8}-([a-z]|[0-9]){4}-([a-z]|[0-9]){4}-([a-z]|[0-9]){12}";
	private static final String REGEX2 = "([a-z]|[0-9]){8}-([a-z]|[0-9]){4}-([a-z]|[0-9]){4}-([a-z]|[0-9]){4}-([a-z]|[0-9]){12}";
	private static final String MASKED = "XXXXXXXX-XXXX-XXXX-XXXXXXXXXXXX";
	
	public static String normalize(String address) {
		if(address.contains(REPLY)) {
			return REPLY;
		} else if(address.matches(REGEX) || address.matches(REGEX2)) {
			return MASKED;
		}
		return address;
	}

	public static <T> NRFutureListenerWrapper<T> getListenerWrapper(Listener<T> listener) {
		if(listener instanceof NRFutureListenerWrapper) {
			return null;
		}
		Token token = NewRelic.getAgent().getTransaction().getToken();
		if(token != null) {
			if(token.isActive()) {
				return new NRFutureListenerWrapper<>(listener,token);
			} else {
				token.expire();
				token = null;
			}
		}
		return null;
	}
	
	public static boolean replyAddress(String address) {
		return address.contains(REPLY);
	}
	
	public static boolean tempAddress(String address) {
		for(int i=0;i<address.length();i++) {
			char c = address.charAt(i);
			if(!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	
	public static void main(String[] args) {
		String check = "5f6a64ff-3892-4362-9a4e-e8abc77171e9";
		boolean b = check.matches(REGEX) || check.matches(REGEX2);
		if(b) {
			System.out.println("matches");
		} else {
			System.out.println("does not match");
		}
	}
}
