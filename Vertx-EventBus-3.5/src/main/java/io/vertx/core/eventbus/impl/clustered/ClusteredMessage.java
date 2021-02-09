package io.vertx.core.eventbus.impl.clustered;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.MessageHeaders;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.impl.CodecManager;
import io.vertx.core.eventbus.impl.MessageImpl;

@Weave
public abstract class ClusteredMessage<U, V> extends MessageImpl<U, V> {
	
	@Trace(async=true)
	public void readFromWire(Buffer buffer, CodecManager codecManager) {
		Weaver.callOriginal();
		MessageHeaders msgHeaders = new MessageHeaders(headers);
		NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, msgHeaders);
	}
}
