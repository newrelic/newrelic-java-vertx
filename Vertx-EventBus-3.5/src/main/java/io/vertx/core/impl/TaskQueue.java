package io.vertx.core.impl;

import java.util.concurrent.Executor;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRRunnableWrapper;

@Weave
public abstract class TaskQueue {

	@Trace
	public void execute(Runnable task, Executor executor) {
		if(NRRunnableWrapper.class.isInstance(task)) {
			NRRunnableWrapper nrRunnable = (NRRunnableWrapper)task;
			Token token = nrRunnable.getToken();
			if(token != null) {
				token.linkAndExpire();
				task = nrRunnable.getDelegate();
				token = null;
				nrRunnable.setToken(null);
			}
		} else {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			String tokenClass = token.getClass().getSimpleName();
			if(!tokenClass.toLowerCase().startsWith("noop")) {
				NRRunnableWrapper wrapper = new NRRunnableWrapper(task,token);
				task = wrapper;
			}
		}
		Weaver.callOriginal();
	}
	
}
