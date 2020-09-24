package com.nr.instrumentation.vertx;

import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRRunnableWrapper implements Runnable {
	
	private Runnable delegate;
	public Runnable getDelegate() {
		return delegate;
	}

	public Token getToken() {
		return token;
	}

	private Token token = null;
	
	public void setToken(Token token) {
		this.token = token;
	}

	public  NRRunnableWrapper(Runnable d,Token t) {
		delegate = d;
		token = t;
	}

	@Override
	@Trace(dispatcher=true,async=true)
	public void run() {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		delegate.run();
	}

	public Token getAndRemoveToken() {
		Token t = token;
		token = null;
		return t;
	}

}
