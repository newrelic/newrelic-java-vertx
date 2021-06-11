package io.vertx.core.eventbus.impl.clustered;

import java.net.URI;
import java.util.HashMap;

import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.MessageHeaders;
import com.nr.instrumentation.vertx.VertxUtils;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.impl.EventBusImpl;
import io.vertx.core.eventbus.impl.MessageImpl;
import io.vertx.core.eventbus.impl.OutboundDeliveryContext;

@Weave
public abstract class ClusteredEventBus extends EventBusImpl {
	
	private String nodeId = Weaver.callOriginal();
	
	@Trace(dispatcher=true)
	protected <T> void sendOrPub(OutboundDeliveryContext<T> sendContext) {
		if (!sendContext.options.isLocalOnly()) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				sendContext.token = token;
			} else if(token != null) {
				token.expire();
				token = null;
			}
		}
		Weaver.callOriginal();
	}

		
	@Trace
	private <T> void clusteredSendReply(String replyDest, OutboundDeliveryContext<T> sendContext) {
		if(!replyDest.equals(nodeId)) {
			Message<?> message = sendContext.message();
			MultiMap headers = message.headers();
			MessageHeaders msgHeaders = new MessageHeaders(headers);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		}
		Weaver.callOriginal();
	}
	
	@SuppressWarnings("rawtypes")
	@Trace(dispatcher=true)
	private void sendRemote(OutboundDeliveryContext<?> sendContext, String remoteNodeId, MessageImpl message) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		String address = message.address();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Address", address);
		attributes.put("RemoteNodeID", remoteNodeId);
		
		
		traced.addCustomAttributes(attributes);
		if(VertxUtils.tempAddress(address)) {
			address = "Temp";
		}
		URI uri = URI.create("vertx://"+remoteNodeId+"/"+address);
		GenericParameters params = GenericParameters.library("Vertx").uri(uri).procedure("sendRemote").build();
		traced.reportAsExternal(params);
		Weaver.callOriginal();
	}
}