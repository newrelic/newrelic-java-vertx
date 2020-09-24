package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRTaskWrapper;

import io.vertx.core.Handler;

@Weave
public abstract class EventLoopContext extends ContextImpl {

	@Trace(excludeFromTransactionTrace=true)
	<T> void execute(T value, Handler<T> task) {
		Weaver.callOriginal();
	}
	
	@Trace(excludeFromTransactionTrace=true)
	void executeAsync(Handler<Void> task) {
		if(!NRTaskWrapper.class.isInstance(task)) {
			NRTaskWrapper<Void> wrapper = new NRTaskWrapper<Void>(task, NewRelic.getAgent().getTransaction().getToken());
			task = wrapper;
		}
		Weaver.callOriginal();
	}
	

}
