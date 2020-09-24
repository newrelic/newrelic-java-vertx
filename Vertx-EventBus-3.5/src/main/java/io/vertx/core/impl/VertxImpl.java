package io.vertx.core.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRCompletionWrapper;
import com.nr.instrumentation.vertx.NRFutureWrapper;

@Weave
public abstract class VertxImpl {

	@Trace(dispatcher=true)
	public void runOnContext(Handler<Void> task) {
		Weaver.callOriginal();
	}
	
	@Trace
	public <T> void executeBlocking(Handler<Future<T>> blockingCodeHandler, boolean ordered,Handler<AsyncResult<T>> asyncResultHandler) {
		Token token = NewRelic.getAgent().getTransaction().getToken();
		NRFutureWrapper<T> wrapper = new NRFutureWrapper<T>(blockingCodeHandler, token);
		NRCompletionWrapper<T> resultWrapper = new NRCompletionWrapper<T>(asyncResultHandler,token,NewRelic.getAgent().getTransaction().startSegment("CompletionHandler"));
		asyncResultHandler = resultWrapper;
		blockingCodeHandler = wrapper;
		
		Weaver.callOriginal();
	}

}
