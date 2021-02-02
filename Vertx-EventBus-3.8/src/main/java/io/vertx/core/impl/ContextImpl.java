package io.vertx.core.impl;

import java.util.concurrent.Executor;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRTaskWrapper;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.spi.metrics.PoolMetrics;

@Weave(type=MatchType.BaseClass)
abstract class ContextImpl {

	@SuppressWarnings("rawtypes")
	@Trace
	<T> void executeBlocking(Handler<Promise<T>> blockingCodeHandler, Handler<AsyncResult<T>> resultHandler,Executor exec, TaskQueue queue, PoolMetrics metrics) {
		if(queue != null) {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ContextImpl",getClass().getSimpleName(),"executeBlocking","queue");
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ContextImpl",getClass().getSimpleName(),"executeBlocking","executor");
		}
		Weaver.callOriginal();
	}
	
	
	@Trace
	void executeAsync(Handler<Void> task) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ContextImpl",getClass().getSimpleName(),"executeAsync");
		if(!(task instanceof NRTaskWrapper)) {
			NRTaskWrapper<Void> wrapper = new NRTaskWrapper<Void>(task, NewRelic.getAgent().getTransaction().getToken());
			task = wrapper;
		}
		Weaver.callOriginal();
	}
	
//	@Trace
//	public void runOnContext(Handler<Void> hTask) {
//		Token token = NewRelic.getAgent().getTransaction().getToken();
//		Segment segment = NewRelic.getAgent().getTransaction().startSegment("runOnContext");
//		if(hTask != null) {
//			NRTaskHandler wrapper = new NRTaskHandler(token, segment, hTask);
//			hTask = wrapper;
//		}
//		Weaver.callOriginal();
//	}
//	
//	
//	@Trace
//	public final <T> void executeFromIO(T value, Handler<T> task) { 
//		Weaver.callOriginal();
//	 }
//	
	@Trace
	<T> void execute(T value, Handler<T> task) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ContextImpl",getClass().getSimpleName(),"execute");
		Weaver.callOriginal();
	}
	
//	@Trace(async=true)
//	<T> boolean executeTask(T arg, Handler<T> hTask) {
//		if(hTask instanceof NRTaskWrapper) {
//			NRTaskWrapper<T> wrapper = (NRTaskWrapper<T>)hTask;
//			Token t = wrapper.getToken();
//			if(t != null) {
//				t.link();
//			}
//		}
//		return Weaver.callOriginal();
//	}
	
}
