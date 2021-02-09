package io.vertx.core.eventbus.impl.clustered;

import java.net.URI;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.api.agent.DistributedTracePayload;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.OutboundWrapper;
import com.nr.instrumentation.vertx.TokenUtils;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.impl.EventBusImpl;
import io.vertx.core.eventbus.impl.MessageImpl;
import io.vertx.core.net.impl.ServerID;
import io.vertx.core.spi.cluster.ChoosableIterable;

@Weave
public abstract class ClusteredEventBus extends EventBusImpl {
	
	private ServerID serverID = Weaver.callOriginal();
	
	@Trace(dispatcher=true)
	protected <T> void sendOrPub(OutboundDeliveryContext<T> sendContext) {
		DeliveryOptions options = sendContext.options;
		
		if(!options.isLocalOnly()) {
			Token t = NewRelic.getAgent().getTransaction().getToken();
			if(t != null && t.isActive()) {
				sendContext.token = t;
			} else if(t != null) {
				t.expire();
				t = null;
			}
		}
	
		Weaver.callOriginal();
	}

	
	@Trace(async=true)
	private <T> void sendToSubs(ChoosableIterable<ClusterNodeInfo> subs, OutboundDeliveryContext<T> sendContext) {
		if(sendContext.token != null) {
			sendContext.token.linkAndExpire();
			sendContext.token = null;
		}
		Message<?> message = sendContext.message();
		if(ClusteredMessage.class.isInstance(message)) {
			ClusteredMessage<?,?> cMessage = (ClusteredMessage<?,?>)message;
			MultiMap headers = cMessage.headers();
			String metadata = null;
			if(TokenUtils.distributedTracingEnabled) {
				
				DistributedTracePayload payload = NewRelic.getAgent().getTransaction().createDistributedTracePayload();
				
				metadata = payload != null ?payload.text() : null;
			} else {
				metadata = NewRelic.getAgent().getTransaction().getRequestMetadata();
			}
			if(metadata != null) {
				headers.add(TokenUtils.REQUESTMETADATA, metadata);
			}
			
		}
		Weaver.callOriginal();
	}
	
	@Trace
	private <T> void clusteredSendReply(ServerID replyDest, OutboundDeliveryContext<T> sendContext) {
		if(!replyDest.equals(serverID)) {
			Message<?> message = sendContext.message();
			MultiMap headers = message.headers();
			String metadata = null;
			if(!TokenUtils.distributedTracingEnabled) {
				DistributedTracePayload payload = NewRelic.getAgent().getTransaction().createDistributedTracePayload();
				metadata = payload != null ?payload.text() : null;
			} else {
				metadata = NewRelic.getAgent().getTransaction().getRequestMetadata();
			}
			if(metadata != null) {
				headers.add(TokenUtils.REQUESTMETADATA, metadata);
			}
		}
		Weaver.callOriginal();
	}
	
	@SuppressWarnings("rawtypes")
	@Trace(dispatcher=true)
	private void sendRemote(ServerID theServerID, MessageImpl message) {
		if(ClusteredMessage.class.isInstance(message)) {
			ClusteredMessage<?,?> cMessage = (ClusteredMessage<?,?>)message;
			MultiMap headers = cMessage.headers();
			String metadata = null;
			if(TokenUtils.distributedTracingEnabled) {
				DistributedTracePayload payload = NewRelic.getAgent().getTransaction().createDistributedTracePayload();
				
				metadata = payload != null ?payload.text() : null;
			} else {
				metadata = NewRelic.getAgent().getTransaction().getRequestMetadata();
			}
			if(metadata != null) {
				headers.add(TokenUtils.REQUESTMETADATA, metadata);
			}
			
		}
		String address = message.address();
		if(TokenUtils.tempAddress(address)) {
			address = "Temp";
		}
		String host = theServerID.host;
		int port = theServerID.port;
		URI uri = URI.create("vertx://"+host+":"+port+"/"+address);
		GenericParameters params = GenericParameters.library("Vertx").uri(uri).procedure("sendRemote").build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		Weaver.callOriginal();
	}
}