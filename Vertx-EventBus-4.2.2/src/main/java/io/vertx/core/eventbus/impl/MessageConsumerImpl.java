package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.NRMessageHandlerWrapper;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

@Weave
public abstract class MessageConsumerImpl<T> {

	public MessageConsumer<T> handler(Handler<Message<T>> handler)  {
		if(handler == null) {
			NRMessageHandlerWrapper<T> wrapper = new NRMessageHandlerWrapper<T>(handler);
			handler = wrapper;
		} else if(!(handler instanceof NRMessageHandlerWrapper)){
			NRMessageHandlerWrapper<T> wrapper = new NRMessageHandlerWrapper<T>(handler);
			handler = wrapper;
		}
		return Weaver.callOriginal();
	}

}
