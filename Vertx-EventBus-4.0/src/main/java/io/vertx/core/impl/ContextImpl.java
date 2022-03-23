package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRTaskWrapper;

import io.netty.util.concurrent.Promise;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@Weave(type=MatchType.BaseClass)
abstract class ContextImpl {
	
	@Trace
	static <T> Future<T> executeBlocking(ContextInternal context, Handler<Promise<T>> blockingCodeHandler, WorkerPool workerPool, TaskQueue queue) {
		if(!NRTaskWrapper.class.isInstance(blockingCodeHandler)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			NRTaskWrapper<Promise<T>> wrapper1 = new NRTaskWrapper<Promise<T>>(blockingCodeHandler, token);
			wrapper1.name = "CodeHandler";
			blockingCodeHandler = wrapper1;
		}
		return Weaver.callOriginal();
	}
	
	@Trace
	static void executeIsolated(Handler<Void> task) {
		Weaver.callOriginal();
	}

	@Trace
	public void runOnContext(Handler<Void> hTask) {
		Weaver.callOriginal();
	}
	
	@Trace
	void runOnContext(AbstractContext ctx, Handler<Void> action) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	<T> void execute(AbstractContext ctx, Runnable task) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	<T> void execute(AbstractContext ctx, T argument, Handler<T> task) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	<T> void emit(AbstractContext ctx, T argument, Handler<T> task) {
		Weaver.callOriginal();
	}
	
	
}
