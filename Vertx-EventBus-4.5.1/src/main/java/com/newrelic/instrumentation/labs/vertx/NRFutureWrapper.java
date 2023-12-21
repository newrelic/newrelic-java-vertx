package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

import io.vertx.core.Handler;
import io.vertx.core.Promise;

public class NRFutureWrapper<T> implements Handler<Promise<T>> {
	
	private Handler<Promise<T>> delegate = null;
	
	private Token token = null;
	
	public NRFutureWrapper(Handler<Promise<T>> h,Token t) {
		delegate = h;
		token = t;
	}

	@Override
	@Trace(async=true)
	public void handle(Promise<T> event) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","FutureHandler","handle"});
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		delegate.handle(event);
	}

}
