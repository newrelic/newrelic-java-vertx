package io.netty.util.concurrent;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.BaseClass)
public abstract class AbstractEventExecutor {

	@Trace(async=true)
	protected static void safeExecute(Runnable task) {
		Weaver.callOriginal();
	}
}
