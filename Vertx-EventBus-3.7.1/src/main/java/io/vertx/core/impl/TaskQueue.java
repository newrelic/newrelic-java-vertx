package io.vertx.core.impl;

import java.util.concurrent.Executor;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class TaskQueue {

	@Trace
	public void execute(Runnable task, Executor executor) {
		Weaver.callOriginal();
	}
	
}
