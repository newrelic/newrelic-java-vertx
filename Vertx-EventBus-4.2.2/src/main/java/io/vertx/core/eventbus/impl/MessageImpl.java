package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.impl.headers.HeadersMultiMap;

@Weave(type=MatchType.BaseClass)
public abstract class MessageImpl<U, V> {

	public abstract String address();
	
	public abstract String replyAddress();
		
	public abstract MultiMap headers();
	
	@Trace(dispatcher=true)
	public <R> void reply(Object message, DeliveryOptions options) {
		MultiMap headers = options.getHeaders();
		if(headers == null) {
			headers = new HeadersMultiMap();
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
			headers = new HeadersMultiMap();
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
