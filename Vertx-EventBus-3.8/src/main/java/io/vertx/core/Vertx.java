package io.vertx.core;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.VertxCoreUtil;

@Weave
public class Vertx {
	
	public static Vertx vertx() {
		if(!VertxCoreUtil.initialized) {
			VertxCoreUtil.init();
		}
		return Weaver.callOriginal();
	}
	
	public static Vertx vertx(VertxOptions options) {
		if(!VertxCoreUtil.initialized) {
			VertxCoreUtil.init();
		}
		return Weaver.callOriginal();
	}
}
