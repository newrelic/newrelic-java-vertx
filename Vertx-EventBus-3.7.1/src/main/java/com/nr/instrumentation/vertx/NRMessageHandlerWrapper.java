package com.nr.instrumentation.vertx;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

public class NRMessageHandlerWrapper<T> implements Handler<Message<T>> {
	
	private Handler<Message<T>> delegate = null;
	private Token token = null;
	private Segment segment = null;
	
	private static boolean isTransformed = false;
	
	
	public NRMessageHandlerWrapper(Handler<Message<T>> d,Token t, Segment s) {
		delegate = d;
		token = t;
		segment = s;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(dispatcher=true,async=true)
	public void handle(Message<T> event) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MessageHandler","handle"});
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
