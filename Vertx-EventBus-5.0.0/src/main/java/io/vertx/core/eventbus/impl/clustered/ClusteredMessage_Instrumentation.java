package io.vertx.core.eventbus.impl.clustered;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;

import io.vertx.core.eventbus.impl.MessageImpl_Instrumentation;

@Weave(originalName = "io.vertx.core.eventbus.impl.clustered.ClusteredMessage")
public abstract class ClusteredMessage_Instrumentation<U, V> extends MessageImpl_Instrumentation<U, V> {

	@SuppressWarnings("unused")
	private void decodeHeaders() {
		Weaver.callOriginal();
		if(headers() != null) {
			MessageHeaders msgHeaders = new MessageHeaders(headers());
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, msgHeaders);
		}
	}
}
