package com.newrelic.instrumentation.labs.vertx;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;

public class TokenUtils implements Runnable {

	private static TokenUtils instance = null;
	
	static {
		instance = new TokenUtils();
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(instance, 60, 30, TimeUnit.SECONDS);
	}
	
	private static ConcurrentHashMap<String, Token> tokenCache = new ConcurrentHashMap<String, Token>();
	private static ConcurrentHashMap<String, Long> tokentimeCache = new ConcurrentHashMap<String, Long>();
	private static final long timeout = 2 * 60 * 1000L;
	
	public static final String TOKENHASH = "TokenHash";
	
	public static final String REQUESTMETADATA = "NRRequestMetadata";
	
	public static final String RESPONSEMETADATA = "NRResponseMetadata";
	
	
	public static Token getToken(String hash) {
		Token token = tokenCache.remove(hash);
		tokentimeCache.remove(hash);
		return token;
	}
	
	public static void addToken(String key, Token token) {
		tokentimeCache.put(key, System.currentTimeMillis());
		tokenCache.put(key, token);
	}
	
	@Override
	public void run() {
		NewRelic.getAgent().getLogger().log(Level.FINEST, "Running Token Cache Purge");
		synchronized (tokentimeCache) {
			KeySetView<String, Long> keys = tokentimeCache.keySet();
			long current = System.currentTimeMillis();
			for(String key : keys) {
				long timestamp = tokentimeCache.get(key);
				if(current - timestamp > timeout) {
					tokentimeCache.remove(key);
					Token token = tokenCache.remove(key);
					NewRelic.getAgent().getLogger().log(Level.FINEST, "Removed and expired token {0} associated with token hash {1}",token,key);
					token.expire();
					token = null;
				}
			}
			Integer size = tokenCache.size();
			NewRelic.getAgent().getMetricAggregator().recordMetric("Custom/Token Caches/Token Cache Size", size.floatValue());
		}
		
		
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

	
}
