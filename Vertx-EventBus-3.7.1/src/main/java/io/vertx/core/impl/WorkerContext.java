package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRTaskWrapper;

import io.vertx.core.Handler;

@Weave
abstract class WorkerContext extends ContextImpl {

	@Trace(excludeFromTransactionTrace=true)
	<T> void execute(T value, Handler<T> task) {
		if(!NRTaskWrapper.class.isInstance(task)) {
			NRTaskWrapper<T> wrapper = new NRTaskWrapper<T>(task, NewRelic.getAgent().getTransaction().getToken());
			task = wrapper;
		}
		Weaver.callOriginal();
	}
	
	@Trace(excludeFromTransactionTrace=true)
	void executeAsync(Handler<Void> task) {
		Weaver.callOriginal();
	}
	

}
