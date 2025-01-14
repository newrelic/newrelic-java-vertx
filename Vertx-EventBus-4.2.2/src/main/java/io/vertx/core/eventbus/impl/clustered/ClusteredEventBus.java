package io.vertx.core.eventbus.impl.clustered;

import java.net.URI;
import java.util.HashMap;

import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.DeliveryOptionsHeaders;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;
import com.newrelic.instrumentation.labs.vertx.VertxUtils;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.impl.MessageImpl;
import io.vertx.core.eventbus.impl.OutboundDeliveryContext;

@Weave
public abstract class ClusteredEventBus {

	private String nodeId =Weaver.callOriginal();
	private String getClusterHost() {
		return Weaver.callOriginal();
	}
	
	 private int getClusterPort() {
		 return Weaver.callOriginal();
	 }


	@Trace(dispatcher=true)
	protected <T> void sendOrPub(OutboundDeliveryContext<T> sendContext) {
		if(sendContext != null) {
			if(sendContext.options != null) {
				DeliveryOptionsHeaders wrapper = new DeliveryOptionsHeaders(sendContext.options);
				NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(wrapper);
			}
		}
		Weaver.callOriginal();
	}

	@Trace(async=true)
	private <T> void sendToNode(OutboundDeliveryContext<T> sendContext, String nodeId) {
		Weaver.callOriginal();
	}

	@SuppressWarnings("rawtypes")
	public MessageImpl createMessage(boolean send, String address, MultiMap headers, Object body, String codecName) {
		MessageHeaders nrHeaders = new MessageHeaders(headers);
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(nrHeaders);
		headers = nrHeaders.getMultimap();
		return Weaver.callOriginal();
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
	private void sendRemote(OutboundDeliveryContext<?> sendContext, String remoteNodeId, MessageImpl message)  {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		String address = message.address();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Address", address);
		attributes.put("RemoteNodeId", remoteNodeId);

		traced.addCustomAttributes(attributes);
		address = VertxUtils.normalize(address);
		String clusterHost = getClusterHost();
		int clusterPort = getClusterPort();
		StringBuffer sb = new StringBuffer("vertx://");
		if(clusterHost != null && !clusterHost.isEmpty()) {
			sb.append(clusterHost);
		}
		if(clusterPort > 0) {
			sb.append(':');
			sb.append(clusterPort);
		}
		sb.append('/');
		sb.append(address);
		
		URI uri = URI.create(sb.toString());
		GenericParameters params = GenericParameters.library("Vertx").uri(uri).procedure("sendRemote").build();
		traced.reportAsExternal(params);
		Weaver.callOriginal();
	}
}