package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import java.util.logging.Level;

@Weave(originalName = "io.vertx.core.eventbus.impl.DeliveryContextBase", type = MatchType.BaseClass)
abstract class DeliveryContextBase_Instrumentation {

    @Trace
    void dispatch() {
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom","DeliveryContext", getClass().getSimpleName(),"dispatch");
        Weaver.callOriginal();
    }

    @Trace(dispatcher = true)
    protected void execute() {
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom","DeliveryContext", getClass().getSimpleName(),"execute");
        Weaver.callOriginal();
    }

    @Trace(dispatcher = true)
    public void next() {
        NewRelic.getAgent().getLogger().log(Level.FINE,new Exception("Call to DeliveryContext next"),"Call to {0} next",getClass().getSimpleName());
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom","DeliveryContext", getClass().getSimpleName(),"next");
        Weaver.callOriginal();
    }
}
