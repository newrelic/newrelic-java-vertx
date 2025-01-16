package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.NRCompletionListener;
import com.newrelic.instrumentation.labs.vertx.NRTaskWrapper;

import io.netty.util.concurrent.Promise;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.future.FutureInternal;

@Weave
public abstract class ContextImpl {
	
	@Trace
	private static <T> Future<T> internalExecuteBlocking(ContextInternal context, Handler<Promise<T>> blockingCodeHandler, WorkerPool workerPool, TaskQueue queue) {
		if(!NRTaskWrapper.class.isInstance(blockingCodeHandler)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			NRTaskWrapper<Promise<T>> wrapper1 = new NRTaskWrapper<Promise<T>>(blockingCodeHandler, token);
			wrapper1.name = "CodeHandler";
			blockingCodeHandler = wrapper1;
		}
		Future<T> f = Weaver.callOriginal();
		if(f instanceof FutureInternal) {
			Segment segment = NewRelic.getAgent().getTransaction().startSegment("CompletionListener");
			NRCompletionListener<T> listener = new NRCompletionListener<T>(segment);
			((FutureInternal<T>)f).addListener(listener);
		}
		
		return f;
	}
	
	@Trace
	protected void runOnContext(ContextInternal ctx, Handler<Void> action) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	protected <T> void execute(ContextInternal ctx, Runnable task) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	protected <T> void execute(ContextInternal ctx, T argument, Handler<T> task) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	protected <T> void emit(ContextInternal ctx, T argument, Handler<T> task) {
		String taskType = task != null ? task.getClass().getName() : "Null";
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "Context-Emit", "ContextEmit",taskType);
		Weaver.callOriginal();
	}
	
	
}
