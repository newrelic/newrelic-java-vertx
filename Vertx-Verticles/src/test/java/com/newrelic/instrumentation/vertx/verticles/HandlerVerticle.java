package com.newrelic.instrumentation.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;

public class HandlerVerticle extends AbstractVerticle {
	
	MessageConsumer<?> consumer;
	
	public HandlerVerticle(MessageConsumer<?> con) {
		consumer = con;
	}

	@Override
	public void start() throws Exception {
		vertx.setPeriodic(5L, v -> {
	    	fetch();
	    	});	}

	@Override
	public void stop() throws Exception {
	}

	public void fetch() {
		consumer.fetch(6);
	}
	
}
