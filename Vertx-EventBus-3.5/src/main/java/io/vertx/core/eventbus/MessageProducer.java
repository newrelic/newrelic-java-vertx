package io.vertx.core.eventbus;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.NRWrappedReplyHandler;

@Weave(type=MatchType.Interface)
public abstract class MessageProducer<T> {

	@Trace
	public <R> MessageProducer<T> send(T message, Handler<AsyncResult<Message<R>>> replyHandler){
		Token token = NewRelic.getAgent().getTransaction().getToken();
		Segment segment = NewRelic.getAgent().getTransaction().startSegment("MessageProducer-Send");
		NRWrappedReplyHandler<R> wrapped = new NRWrappedReplyHandler<R>(token, segment, replyHandler);
		replyHandler = wrapped;
		return Weaver.callOriginal();
	}
	
	@Trace
	public MessageProducer<T> send(T message) {
		return Weaver.callOriginal();
	}

}
