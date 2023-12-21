package io.vertx.core.eventbus.impl;

import java.util.concurrent.ConcurrentMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.impl.utils.ConcurrentCyclicSequence;

@SuppressWarnings({"rawtypes"})
@Weave(type=MatchType.BaseClass)
public abstract class EventBusImpl  {

	protected final ConcurrentMap<String, ConcurrentCyclicSequence<HandlerHolder>> handlerMap = Weaver.callOriginal();

	@Trace(dispatcher=true)
	public <T> EventBus send(String address, Object message, DeliveryOptions options) {
		MultiMap headers = options.getHeaders();
		if(headers == null) {
			options.setHeaders(new HeadersMultiMap());
		}
		MessageHeaders msgHeaders = new MessageHeaders(options.getHeaders());
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		options.setHeaders(msgHeaders.getMultimap());
		
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","send"});
		NewRelic.getAgent().getTracedMethod().addCustomAttribute("address", address);
		return Weaver.callOriginal();
	}

	@Trace
	public MessageImpl createMessage(boolean send, boolean localOnly, String address, MultiMap headers, Object body, String codecName) {
		
		MessageHeaders msgHeaders;
		if(headers != null) {
			msgHeaders = new MessageHeaders(headers);
		} else {
			msgHeaders = new MessageHeaders(new HeadersMultiMap());
		}
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		headers = msgHeaders.getMultimap();
		MessageImpl msg = Weaver.callOriginal();
		return msg;
	}

	@Trace(async=true)
	protected void callCompletionHandlerAsync(Handler<AsyncResult<Void>> completionHandler) {
		
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public EventBus publish(String address, Object message, DeliveryOptions options) {
		MultiMap headers = options.getHeaders();
		if(headers == null) {
			options.setHeaders(new HeadersMultiMap());
		}
		MessageHeaders msgHeaders = new MessageHeaders(options.getHeaders());
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		options.setHeaders(msgHeaders.getMultimap());
		
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","publish"});
		NewRelic.getAgent().getTracedMethod().addCustomAttribute("Address", address);
		return Weaver.callOriginal();
	}

	@Trace(dispatcher = true)
	protected <T> void sendReply(MessageImpl replyMessage, DeliveryOptions options, ReplyHandler<T> replyHandler) {
		
		MultiMap headers = options.getHeaders();
		if(headers == null) {
			options.setHeaders(new HeadersMultiMap());
		}
		MessageHeaders msgHeaders = new MessageHeaders(options.getHeaders());
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		options.setHeaders(msgHeaders.getMultimap());
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		
		traced.setMetricName(new String[] {"Custom","EventBusImpl","sendReply"});
		if(replyMessage != null && replyMessage.address() != null) {
			traced.addCustomAttribute("Reply-Address", replyMessage.address());
		}
		Weaver.callOriginal();
	}

}
