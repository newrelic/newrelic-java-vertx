package io.vertx.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveIntoAllMethods;

@Weave(type=MatchType.BaseClass)
public abstract class AbstractVerticle {

	@WeaveIntoAllMethods
	@Trace
	private static void instrumentation() {
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		StackTraceElement first = traces[1];
		String methodName = first.getMethodName();
		String classname = first.getClassName();
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Vertx","Verticle",classname,methodName});
	}
}
