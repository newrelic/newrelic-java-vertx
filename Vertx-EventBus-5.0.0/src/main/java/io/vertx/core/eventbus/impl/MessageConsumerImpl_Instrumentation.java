package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;
import com.newrelic.instrumentation.labs.vertx.NRMessageHandlerWrapper;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.internal.ContextInternal;

@Weave(originalName = "io.vertx.core.eventbus.impl.MessageConsumerImpl")
public abstract class MessageConsumerImpl_Instrumentation<T> {

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

	@Trace(async=true)
	protected void doReceive(Message<T> message) {
		if(message instanceof MessageImpl_Instrumentation) {
			MessageImpl_Instrumentation msg = (MessageImpl_Instrumentation) message;
			if (msg.token != null) {
				if (msg.replyAddress() != null) {
					msg.token.link();
				} else {
					msg.token.linkAndExpire();
					msg.token = null;
				}
			}
		}
		Weaver.callOriginal();
	}

	@Trace
	protected void dispatch(Message<T> msg, ContextInternal context, Handler<Message<T>> handler) {
		Weaver.callOriginal();
	}
}
