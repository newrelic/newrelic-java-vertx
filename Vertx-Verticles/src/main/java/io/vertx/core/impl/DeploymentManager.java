package io.vertx.core.impl;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;

@Weave
public abstract class DeploymentManager {

	@Trace
	private void doDeploy(String identifier,
            DeploymentOptions options,
            ContextInternal parentContext,
            ContextInternal callingContext,
            Handler<AsyncResult<String>> completionHandler,
            ClassLoader tccl, Verticle... verticles) {
		
	}
}
