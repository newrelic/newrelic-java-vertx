package io.vertx.core.impl;

import io.vertx.core.Handler;
import io.vertx.core.spi.metrics.PoolMetrics;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRRunnableWrapper;
import com.nr.instrumentation.vertx.NRTaskHandler;

@Weave(type=MatchType.BaseClass)
public abstract class ContextImpl {

	@Trace
	protected void executeAsync(Handler<Void> task) {
		Weaver.callOriginal();
	}
	
	@Trace
	public void runOnContext(Handler<Void> hTask) {
		Token token = NewRelic.getAgent().getTransaction().getToken();
		Segment segment = NewRelic.getAgent().getTransaction().startSegment("runOnContext");
		if(hTask != null) {
			NRTaskHandler wrapper = new NRTaskHandler(token, segment, hTask);
			hTask = wrapper;
		}
		Weaver.callOriginal();
	}
	
	
	@Trace
	 public void executeFromIO(ContextTask task) {
		Weaver.callOriginal();
	 }
	
	@SuppressWarnings("rawtypes")
	@Trace
	protected Runnable wrapTask(ContextTask cTask, Handler<Void> hTask, boolean checkThread, PoolMetrics metrics) {
		Token token = NewRelic.getAgent().getTransaction().getToken();
		Runnable runnable = Weaver.callOriginal();
		NRRunnableWrapper wrapper = new NRRunnableWrapper(runnable,token);
		return wrapper;
	}
	
}
