package io.vertx.core.eventbus.impl;

import java.util.concurrent.ConcurrentMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.DeliveryOptionsHeaders;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;

import com.newrelic.instrumentation.labs.vertx.NRErrorHandler;
import com.newrelic.instrumentation.labs.vertx.NRResultHandler;
import com.newrelic.instrumentation.labs.vertx.NRSegmentHolder;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.impl.utils.ConcurrentCyclicSequence;

@SuppressWarnings({"rawtypes"})
@Weave(type=MatchType.BaseClass, originalName = "io.vertx.core.eventbus.impl.EventBusImpl")
public abstract class EventBusImpl_Instrumentation {

	protected final ConcurrentMap<String, ConcurrentCyclicSequence<HandlerHolder_Instrumentation>> handlerMap = Weaver.callOriginal();

	@Trace(dispatcher=true)
	public <T> EventBus send(String address, Object message, DeliveryOptions options) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","send"});
		NewRelic.getAgent().getTracedMethod().addCustomAttribute("address", address);
		return Weaver.callOriginal();
	}

	@Trace
	public MessageImpl_Instrumentation createMessage(boolean send, boolean localOnly, String address, MultiMap headers, Object body, String codecName) {
		if(headers == null) {
			headers = HeadersMultiMap.httpHeaders();
			MessageHeaders msgHeaders = new MessageHeaders(headers);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		}
		MessageImpl_Instrumentation msg = Weaver.callOriginal();
		return msg;
	}

	@Trace(dispatcher=true)
	public EventBus publish(String address, Object message, DeliveryOptions options) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","publish"});
		NewRelic.getAgent().getTracedMethod().addCustomAttribute("Address", address);
		return Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public <T> Future<Message<T>> request(String address, Object message, DeliveryOptions options) {
		NRSegmentHolder holder = new NRSegmentHolder("EventBusImpl/request");
		Future<Message<T>> result = Weaver.callOriginal();

		return result.onComplete(new NRResultHandler<>(holder), new NRErrorHandler(holder));
	}

	@Trace(dispatcher = true)
	protected <T> void sendReply(MessageImpl_Instrumentation replyMessage, DeliveryOptions options, ReplyHandler<T> replyHandler) {

		TracedMethod traced = NewRelic.getAgent().getTracedMethod();

		traced.setMetricName(new String[] {"Custom","EventBusImpl","sendReply"});
		if(replyMessage != null && replyMessage.address() != null) {
			traced.addCustomAttribute("Reply-Address", replyMessage.address());
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	protected <T> void sendOrPub(OutboundDeliveryContext<T> sendContext) {
		if(sendContext != null) {
			if(sendContext.options != null) {
				DeliveryOptionsHeaders wrapper = new DeliveryOptionsHeaders(sendContext.options);
				NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(wrapper);
			}
		}
		Weaver.callOriginal();
	}



}
