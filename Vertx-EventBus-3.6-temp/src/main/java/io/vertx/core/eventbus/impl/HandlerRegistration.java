package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.MessageHeaders;
import com.nr.instrumentation.vertx.NRCompletionWrapper;
import com.nr.instrumentation.vertx.NRMessageHandlerWrapper;
import com.nr.instrumentation.vertx.TokenUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.impl.clustered.ClusteredMessage;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.spi.metrics.EventBusMetrics;

@Weave
public abstract class HandlerRegistration<T> implements MessageConsumer<T>, Handler<Message<T>> {

	@NewField
	public Token token = null;
	
	 @SuppressWarnings("rawtypes")
	public HandlerRegistration(Vertx vertx, EventBusMetrics metrics, EventBusImpl eventBus, String address, String repliedAddress, boolean localOnly,
             Handler<AsyncResult<Message<T>>> asyncResultHandler, long timeout) {
		 
	 }
	 
	 public MessageConsumer<T> handler(Handler<Message<T>> handler)  {
		 if(!NRMessageHandlerWrapper.class.isInstance(handler)) {
			 NRMessageHandlerWrapper<T> wrapper = new NRMessageHandlerWrapper<T>(handler, NewRelic.getAgent().getTransaction().getToken(), NewRelic.getAgent().getTransaction().startSegment("MessageHandler"));
			 handler = wrapper;
		 }
		 return Weaver.callOriginal();
	 }


	@Trace(async=true)
	public void handle(Message<T> message) {
		if(ClusteredMessage.class.isInstance(message)) {
			ClusteredMessage<?,?> cMessage = (ClusteredMessage<?,?>)message;
			MultiMap headers = cMessage.headers();
			MessageHeaders msgHeaders = new MessageHeaders(headers);
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, msgHeaders);
		}
		String msgAddress = message.address();
		boolean temp = TokenUtils.tempAddress(msgAddress);
		if(!temp) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","HandlerRegistration","handle",msgAddress});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","HandlerRegistration","handle","Temp"});
		}
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}
	
	@Trace(async=true)
	private void doUnregister(Handler<AsyncResult<Void>> completionHandler) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	private void callCompletionHandlerAsync(Handler<AsyncResult<Void>> completionHandler) {
		if(!NRCompletionWrapper.class.isInstance(completionHandler)) {
			NRCompletionWrapper<Void> wrapper = new NRCompletionWrapper<Void>(completionHandler,NewRelic.getAgent().getTransaction().getToken(),NewRelic.getAgent().getTransaction().startSegment("CompletionHandler"));
			completionHandler = wrapper;
			
		} else {
			NRCompletionWrapper<Void> wrapper = (NRCompletionWrapper<Void>)completionHandler;
			if(wrapper.token == null) {
				wrapper.token = NewRelic.getAgent().getTransaction().getToken();
			}
		}
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	public synchronized void completionHandler(Handler<AsyncResult<Void>> completionHandler) {
		if(!NRCompletionWrapper.class.isInstance(completionHandler)) {
			NRCompletionWrapper<Void> wrapper = new NRCompletionWrapper<Void>(completionHandler,NewRelic.getAgent().getTransaction().getToken(),NewRelic.getAgent().getTransaction().startSegment("CompletionHandler"));
			completionHandler = wrapper;
		}
		Weaver.callOriginal();
	}

	@Trace(async=true)
	private void deliver(Handler<Message<T>> theHandler, Message<T> message, ContextInternal context) {
		String msgAddress = message.address();
		boolean temp = TokenUtils.tempAddress(msgAddress);
		if (!temp) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] { "Custom", "HandlerRegistration","deliver", message.address() });
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] { "Custom", "HandlerRegistration","deliver", "Temp" });
		}
		if(message.replyAddress() == null) {
			MultiMap headers = message.headers();
			String hash = headers.get(TokenUtils.TOKENHASH);
			if(hash != null && !hash.isEmpty()) {
				Token token = TokenUtils.getToken(hash);
				if(token != null) {
					token.linkAndExpire();
					token = null;
				}
			}
		}
		Weaver.callOriginal();
	}


	@Trace(dispatcher=true)
	public MessageConsumer<T> endHandler(Handler<Void> endHandler) {
		return Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
  	public synchronized MessageConsumer<T> resume() {
		 return Weaver.callOriginal();
	}

}
