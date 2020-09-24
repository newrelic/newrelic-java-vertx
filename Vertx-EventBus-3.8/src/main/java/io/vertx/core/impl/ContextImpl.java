package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRTaskHandler;

import io.vertx.core.Handler;

@Weave(type=MatchType.BaseClass)
abstract class ContextImpl {

	  
	@Trace
	void executeAsync(Handler<Void> task) {
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
	public final <T> void executeFromIO(T value, Handler<T> task) { 
		Weaver.callOriginal();
	 }
	
	@Trace
	<T> void execute(T value, Handler<T> task) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	<T> boolean executeTask(T arg, Handler<T> hTask) {
		return Weaver.callOriginal();
	}
	
}
