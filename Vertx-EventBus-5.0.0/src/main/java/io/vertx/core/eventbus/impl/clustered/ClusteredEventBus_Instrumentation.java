package io.vertx.core.eventbus.impl.clustered;

import java.net.URI;
import java.util.HashMap;

import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;
import com.newrelic.instrumentation.labs.vertx.VertxUtils;

import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.impl.MessageImpl_Instrumentation;

@Weave(originalName = "io.vertx.core.eventbus.impl.clustered.ClusteredEventBus")
public abstract class ClusteredEventBus_Instrumentation {

	private String nodeId =Weaver.callOriginal();
	private String getClusterHost() {
		return Weaver.callOriginal();
	}
	
	 private int getClusterPort() {
		 return Weaver.callOriginal();
	 }


	@Trace(async=true)
	private <T> void sendToNode(String nodeId, MessageImpl_Instrumentation<?, T> message, Promise<Void> writePromise) {
		Weaver.callOriginal();
	}


	@Trace
	private <T> void clusteredSendReply(MessageImpl_Instrumentation<?, T> message, Promise<Void> writePromise, String replyDest) {
		if(!replyDest.equals(nodeId)) {
			MultiMap headers = message.headers();
			MessageHeaders msgHeaders = new MessageHeaders(headers);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(msgHeaders);
		}
		Weaver.callOriginal();
	}

	@SuppressWarnings("rawtypes")
	@Trace(dispatcher=true)
	private void sendRemote(String remoteNodeId, MessageImpl_Instrumentation<?, ?> message, Promise<Void> writePromise)  {
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