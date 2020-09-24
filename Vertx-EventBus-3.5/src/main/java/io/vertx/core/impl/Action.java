package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class Action<T>{

	@Trace(dispatcher=true)
	public T perform() {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Action",getClass().getSimpleName(),"perform"});
		return Weaver.callOriginal();
	}
}
