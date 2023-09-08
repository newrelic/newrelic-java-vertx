package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

public class NRMessageHandlerWrapper<T> implements Handler<Message<T>> {
	
	private Handler<Message<T>> delegate = null;
	
	public NRMessageHandlerWrapper(Handler<Message<T>> d) {
		delegate = d;
	}

	@Override
	@Trace(dispatcher=true)
	public void handle(Message<T> event) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MessageHandler","handle"});
		delegate.handle(event);
	}

}
