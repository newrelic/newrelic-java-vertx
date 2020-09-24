package io.vertx.core.eventbus;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@Weave(type=MatchType.Interface)
public abstract class MessageProducer<T> {

	@Trace
	public <R> MessageProducer<T> send(T message, Handler<AsyncResult<Message<R>>> replyHandler){
		return Weaver.callOriginal();
	}
	
	@Trace
	public MessageProducer<T> send(T message) {
		return Weaver.callOriginal();
	}

}
