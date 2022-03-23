package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.MessageHeaders;
import com.nr.instrumentation.vertx.VertxUtils;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.ContextInternal;

@Weave(type = MatchType.BaseClass)
public abstract class HandlerRegistration<T>  {

	@NewField
	public Token token = null;

	private final String address = Weaver.callOriginal();

	HandlerRegistration(ContextInternal context,EventBusImpl bus,String address,boolean src) 
	{

	}


	@SuppressWarnings("rawtypes")
	@Trace(dispatcher=true)
	void receive(MessageImpl msg) {
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "EventBus", "receiveMessage",VertxUtils.normalize(address));
		MultiMap headers = msg.headers();
		MessageHeaders msgHeaders = new MessageHeaders(headers);
		NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, msgHeaders);
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","HandlerRegistration","handle"});
		NewRelic.getAgent().getTracedMethod().addCustomAttribute("Handler Address", address);
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	protected boolean doReceive(Message<T> msg) {
		MultiMap headers = msg.headers();
		if(headers != null) {
			MessageHeaders msgHeaders = new MessageHeaders(headers);
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, msgHeaders);
		}
		
		return Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	protected void dispatch(Message<T> msg, ContextInternal context, Handler<Message<T>> handler) {
		Weaver.callOriginal();
	}



}
