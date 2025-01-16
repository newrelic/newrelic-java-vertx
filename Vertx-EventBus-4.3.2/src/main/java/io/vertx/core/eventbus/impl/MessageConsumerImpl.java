package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;
import com.newrelic.instrumentation.labs.vertx.NRMessageHandlerWrapper;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.ContextInternal;

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

	@Trace(dispatcher = true)
	protected boolean doReceive(Message<T> message) {
		MultiMap headers = message.headers();
		MessageHeaders msgHeaders = new MessageHeaders(headers);
		NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, msgHeaders);
		
		return Weaver.callOriginal();
	}
	
	@Trace
	protected void dispatch(Message<T> msg, ContextInternal context, Handler<Message<T>> handler) {
		Weaver.callOriginal();
	}
}
