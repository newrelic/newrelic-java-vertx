package io.vertx.core.http.impl;

import static com.nr.instrumentation.vertx.VertxCoreUtil.END;
import static com.nr.instrumentation.vertx.VertxCoreUtil.VERTX_CLIENT;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRCompletionWrapper;
import com.nr.instrumentation.vertx.OutboundWrapper;

import io.netty.buffer.ByteBuf;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

@Weave(originalName = "io.vertx.core.http.impl.HttpClientRequestImpl")
public abstract class HttpClientRequestImpl_Instrumentation extends HttpClientRequestBase_Instrumentation  {

	@Trace
	private void write(ByteBuf buff, boolean end, Handler<AsyncResult<Void>> completionHandler) {
        if (AgentBridge.getAgent().getTransaction(false) != null) {
            segment = NewRelic.getAgent().getTransaction().startSegment(VERTX_CLIENT, END);
            segment.addOutboundRequestHeaders(new OutboundWrapper(headers()));
        }
		Token token = null;
		if(segment != null) {
			token = segment.getTransaction().getToken();
		} else {
			Token t = NewRelic.getAgent().getTransaction().getToken();
			if(t != null && t.isActive()) {
				token = t;
			} else if(t != null) {
				t.expire();
				t = null;
			}
		}
		NRCompletionWrapper<Void> wrapper = new NRCompletionWrapper<Void>(completionHandler,token,null);
		completionHandler = wrapper;
		Weaver.callOriginal();
	}

	public abstract MultiMap headers();
}