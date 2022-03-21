package io.vertx.core.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;

public abstract class DeploymentManager {

	private void doDeploy(String identifier,
            DeploymentOptions options,
            ContextInternal parentContext,
            ContextInternal callingContext,
            Handler<AsyncResult<String>> completionHandler,
            ClassLoader tccl, Verticle... verticles) {
		
	}
}
