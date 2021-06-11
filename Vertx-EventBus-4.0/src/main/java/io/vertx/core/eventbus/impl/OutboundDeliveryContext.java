package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.ContextInternal;

@Weave
public abstract class OutboundDeliveryContext<T> {

	public final DeliveryOptions options = Weaver.callOriginal();
	
	@NewField
	public Token token = null;
	
	@SuppressWarnings("rawtypes")
	OutboundDeliveryContext(ContextInternal ctx, MessageImpl message, DeliveryOptions options, ReplyHandler<T> replyHandler, Promise<Void> writePromise) {
		
	}

	public abstract Message<T> message();
}
