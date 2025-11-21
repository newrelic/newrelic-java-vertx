package io.vertx.core.impl;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import io.vertx.core.Future;

import java.util.concurrent.Callable;

@Weave(originalName = "io.vertx.core.impl.ContextImpl")
public abstract class ContextImpl_Instrumentation {
	
	@Trace
	public <T> Future<T> executeBlocking(Callable<T> blockingCodeHandler, boolean ordered) {
		Future<T> f = Weaver.callOriginal();
		return f;
	}
}
