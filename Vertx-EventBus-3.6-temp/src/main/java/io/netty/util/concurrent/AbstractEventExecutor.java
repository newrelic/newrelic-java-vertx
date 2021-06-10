package io.netty.util.concurrent;

import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRRunnableWrapper;

@Weave(type=MatchType.BaseClass)
public abstract class AbstractEventExecutor {

	@Trace(async=true)
	protected static void safeExecute(Runnable task) {
		if(NRRunnableWrapper.class.isInstance(task)) {
			NRRunnableWrapper wrapper = (NRRunnableWrapper)task;
			Token token = wrapper.getAndRemoveToken();
			if(token != null) {
				token.linkAndExpire();
			}
		}
		Weaver.callOriginal();
	}
}
