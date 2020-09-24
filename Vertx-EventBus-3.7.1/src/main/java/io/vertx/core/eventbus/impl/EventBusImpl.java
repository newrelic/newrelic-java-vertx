package io.vertx.core.eventbus.impl;

import java.util.concurrent.ConcurrentMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRWrappedReplyHandler;
import com.nr.instrumentation.vertx.TokenUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryContext;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.impl.clustered.ClusteredMessage;
import io.vertx.core.impl.utils.ConcurrentCyclicSequence;

@SuppressWarnings({"rawtypes","unchecked"})
@Weave(type=MatchType.BaseClass)
public abstract class EventBusImpl implements EventBus {

	protected final ConcurrentMap<String, ConcurrentCyclicSequence<HandlerHolder>> handlerMap = Weaver.callOriginal();

	@Trace
	public <T> MessageConsumer<T> consumer(String address) {
		return Weaver.callOriginal();
	}
	
	@Trace
	public <T> MessageConsumer<T> localConsumer(String address, Handler<Message<T>> handler) {
		return Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public <T> EventBus send(String address, Object message, DeliveryOptions options, Handler<AsyncResult<Message<T>>> replyHandler) {
		if (replyHandler != null) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			Segment segment = NewRelic.getAgent().getTransaction().startSegment("EventBus-Send");
			NRWrappedReplyHandler<T> wrapper = new NRWrappedReplyHandler<T>(token, segment, replyHandler);
			replyHandler = wrapper;
		}
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","send"});
		return Weaver.callOriginal();
	}

	@Trace(excludeFromTransactionTrace=true)
	public MessageImpl createMessage(boolean send, String address, MultiMap headers, Object body, String codecName, Handler<AsyncResult<Void>> writeHandler) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","createMessage"});
		MessageImpl msg = Weaver.callOriginal();
		if(!ClusteredMessage.class.isInstance(msg)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			Integer hashCode = token.hashCode();
			MultiMap msgheaders = msg.headers();

			msgheaders.add(TokenUtils.TOKENHASH, hashCode.toString());
			msg.headers = msgheaders;

			TokenUtils.addToken(hashCode.toString(), token);
		}
		return msg;
	}

	@Trace(dispatcher=true)
	protected void callCompletionHandlerAsync(Handler<AsyncResult<Void>> completionHandler) {
		
		Weaver.callOriginal();
	}

	@Trace(excludeFromTransactionTrace=true)
	private <T> void deliverToHandler(MessageImpl msg, HandlerHolder<T> holder) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","deliverToHandler"});

		if(holder.token == null) {
			holder.token = NewRelic.getAgent().getTransaction().getToken();
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public EventBus publish(String address, Object message, DeliveryOptions options) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","publish"});
		return Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	protected <T> void sendReply(OutboundDeliveryContext<T> sendContext, MessageImpl replierMessage) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","sendReply"});
		Weaver.callOriginal();
	}

	@Trace(async=true)
	protected <T> void sendReply(MessageImpl replyMessage, MessageImpl replierMessage, DeliveryOptions options, Handler<AsyncResult<Message<T>>> replyHandler) {
		if (replyHandler != null) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			Segment segment = NewRelic.getAgent().getTransaction().startSegment("EventBus-SendReply");
			NRWrappedReplyHandler<T> wrapper = new NRWrappedReplyHandler<T>(token, segment, replyHandler);
			replyHandler = wrapper;
		}
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","sendReply"});
		Weaver.callOriginal();
	}
	
	@Weave
	protected abstract static class OutboundDeliveryContext<T> implements DeliveryContext<T> {
		
		@Trace(dispatcher=true)
		public void next() {
			Weaver.callOriginal();
		}
		
		@Trace(dispatcher=true)
		public boolean send() {	
			return Weaver.callOriginal();
		}
	}

	@Weave
	protected abstract static class InboundDeliveryContext<T> implements DeliveryContext<T> {
		
		@Trace(dispatcher=true)
		public void next() {
			Weaver.callOriginal();
		}
		
		@Trace(dispatcher=true)
		public boolean send() {	
			return Weaver.callOriginal();
		}
	}

}
