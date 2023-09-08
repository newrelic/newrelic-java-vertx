package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

import io.vertx.core.Handler;

public class NRTaskHandler implements Handler<Void> {
	
	private Token token = null;
	private Segment segment = null;
	private Handler<Void> delegate = null;
	
	public NRTaskHandler(Token t, Segment s, Handler<Void> d) {
		token = t;
		segment = s;
		delegate = d;
	}

	@Override
	@Trace(async=true)
	public void handle(Void event) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(segment != null) {
			segment.end();
			segment = null;
		}
		delegate.handle(event);
	}

}
