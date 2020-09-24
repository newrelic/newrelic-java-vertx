package io.vertx.core.eventbus.impl.clustered;

import java.net.URI;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.TokenUtils;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.impl.CodecManager;
import io.vertx.core.eventbus.impl.MessageImpl;
import io.vertx.core.net.impl.ServerID;

@Weave
public abstract class ClusteredMessage<U, V> extends MessageImpl<U, V> {

	private ServerID sender = Weaver.callOriginal();

	@Trace(async=true)
	public void readFromWire(Buffer buffer, CodecManager codecManager) {
		Weaver.callOriginal();
		if(headers != null) {
			String metadata = headers.get(TokenUtils.REQUESTMETADATA);
			if(metadata != null && !metadata.isEmpty()) {
				if(TokenUtils.distributedTracingEnabled) {
					NewRelic.getAgent().getTransaction().acceptDistributedTracePayload(metadata);
				} else {
					NewRelic.getAgent().getTransaction().processRequestMetadata(metadata);
				}
			} else {
				metadata = headers.get(TokenUtils.RESPONSEMETADATA);
				if(!TokenUtils.distributedTracingEnabled && metadata != null && !metadata.isEmpty()) {
					String address = address();
					if(TokenUtils.tempAddress(address)) {
						address = "Temp";
					}
					String host = sender.host;
					int port = sender.port;
					URI uri = URI.create("vertx://"+host+":"+port+"/"+address);
					NewRelic.getAgent().getTransaction().processResponseMetadata(metadata,uri);
				}
			}
		}
	}
}
