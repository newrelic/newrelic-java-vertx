package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import io.vertx.core.AsyncResult;
import io.vertx.core.Deployable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.function.Supplier;

@Weave(originalName="io.vertx.core.impl.VertxImpl")
public abstract class VertxImpl_instrumentation {

	@Trace
	public Future<String> deployVerticle(Supplier<? extends Deployable> supplier, DeploymentOptions options) {
		return Weaver.callOriginal();
	}
	
	@Trace
	void executeIsolated(Handler<Void> task) {
		Weaver.callOriginal();
	}

	@Weave(originalName="io.vertx.core.impl.VertxImpl$InternalTimerHandler")
	private static class InternalTimerHandler_instrumentation {
		
	    private final Handler<Long> handler = Weaver.callOriginal();

		@Trace
		public void handle(Void v) {
			String handlerClass = handler.getClass().getName();
			if(!ignore(handlerClass)) {
			} else {
				NewRelic.getAgent().getTransaction().ignore();
			}
			Weaver.callOriginal();
		}
		
		@Trace
		public void run() {
			String handlerClass = handler.getClass().getName();
			if(!ignore(handlerClass)) {
			} else {
				NewRelic.getAgent().getTransaction().ignore();
			}
			Weaver.callOriginal();
		}

		private boolean ignore(String classname) {
			if(classname.startsWith("io.vertx.core.http.impl.ConnectionManager")) return true;
			if(classname.startsWith("io.vertx.core.impl.HAManager")) return true;
			return false;
		}
	}
	
}
