package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

@Weave
public abstract class OutboundDeliveryContext<T> {

	public final DeliveryOptions options = Weaver.callOriginal();
	
	@NewField
	public Token token = null;
	
	@SuppressWarnings("rawtypes")
	private OutboundDeliveryContext(MessageImpl message, DeliveryOptions options, HandlerRegistration<T> handlerRegistration, MessageImpl replierMessage) {
		
	}

	public abstract Message<T> message();
}
