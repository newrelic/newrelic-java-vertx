package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import io.vertx.core.internal.ContextInternal;

@Weave(originalName = "io.vertx.core.eventbus.impl.HandlerHolder")
public abstract class HandlerHolder_Instrumentation<T> {

	@NewField
	public Token token = null;
	
	private final HandlerRegistration_Instrumentation<T> handler = Weaver.callOriginal();

	public HandlerHolder_Instrumentation(HandlerRegistration_Instrumentation<T> handler, boolean localOnly, ContextInternal context) {
		
	}
	
	@Trace(async=true)
	public HandlerRegistration_Instrumentation<T> getHandler() {
		HandlerRegistration_Instrumentation<T> hr = Weaver.callOriginal();
		if(token != null) {
			hr.token = token;
			token = null;
		}
		return hr;
	}
	
	public synchronized boolean isRemoved() {
		boolean b = Weaver.callOriginal();
		if(b && token != null) {
			token.expire();
			token = null;
		}
		return b;
	}
}
