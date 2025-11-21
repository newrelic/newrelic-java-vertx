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
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;
import com.newrelic.instrumentation.labs.vertx.VertxUtils;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.internal.ContextInternal;

@Weave(type = MatchType.BaseClass, originalName = "io.vertx.core.eventbus.impl.HandlerRegistration")
public abstract class HandlerRegistration_Instrumentation<T>  {

	@NewField
	public Token token = null;

	private final String address = Weaver.callOriginal();

	HandlerRegistration_Instrumentation(ContextInternal context, EventBusImpl_Instrumentation bus,String address,boolean src)
	{

	}


	@SuppressWarnings("rawtypes")
	@Trace(dispatcher=true)
	void receive(MessageImpl_Instrumentation msg) {
		Token token = NewRelic.getAgent().getTransaction().getToken();

		if(token != null) {
			if(token.isActive()) {
				msg.token = token;
			} else {
				token.expire();
				token = null;
			}
		}
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "EventBus", "receive",VertxUtils.normalize(address));
		MultiMap headers = msg.headers();
		MessageHeaders msgHeaders = new MessageHeaders(headers);
		NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, msgHeaders);
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","HandlerRegistration", getClass().getSimpleName(),"receive"});
		NewRelic.getAgent().getTracedMethod().addCustomAttribute("Handler Address", address);
		Weaver.callOriginal();
	}


}
