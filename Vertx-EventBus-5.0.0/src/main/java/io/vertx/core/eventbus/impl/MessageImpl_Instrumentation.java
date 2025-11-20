package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.impl.headers.HeadersMultiMap;

@Weave(type=MatchType.BaseClass, originalName = "io.vertx.core.eventbus.impl.MessageImpl")
public abstract class MessageImpl_Instrumentation<U, V> {

	public abstract boolean isSend();

	public abstract String address();
	
	public abstract String replyAddress();
		
	public abstract MultiMap headers();

	@NewField
	public Token token = null;

	@WeaveAllConstructors
	public MessageImpl_Instrumentation() {
	}
	
	@Trace(dispatcher=true)
	public <R> void reply(Object message, DeliveryOptions options) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		MultiMap headers = options.getHeaders();
		if(headers == null) {
			headers = HeadersMultiMap.httpHeaders();
		}
		MessageHeaders msgHeaders = new MessageHeaders(headers);
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		options.setHeaders(msgHeaders.getMultimap());
		String replyAddress = replyAddress();
		if(replyAddress != null && !replyAddress.isEmpty()) {
			NewRelic.getAgent().getTracedMethod().addCustomAttribute("ReplyAddress", replyAddress());
		}
		String address = address();
		if(address != null && !address.isEmpty()) {
			NewRelic.getAgent().getTracedMethod().addCustomAttribute("address", address);
		}
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	public <R> Future<Message<R>> replyAndRequest(Object message, DeliveryOptions options) {
		MultiMap headers = options.getHeaders();
		if(headers == null) {
			headers = HeadersMultiMap.httpHeaders();
		}
		MessageHeaders msgHeaders = new MessageHeaders(headers);
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		options.setHeaders(msgHeaders.getMultimap());
		String replyAddress = replyAddress();
		if(replyAddress != null && !replyAddress.isEmpty()) {
			NewRelic.getAgent().getTracedMethod().addCustomAttribute("ReplyAddress", replyAddress());
		}
		String address = address();
		if(address != null && !address.isEmpty()) {
			NewRelic.getAgent().getTracedMethod().addCustomAttribute("address", address);
		}
		return Weaver.callOriginal();
	}
	
}
