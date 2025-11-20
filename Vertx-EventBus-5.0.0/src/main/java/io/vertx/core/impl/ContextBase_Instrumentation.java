package io.vertx.core.impl;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.NRRunnableWrapper;
import com.newrelic.instrumentation.labs.vertx.NRTaskWrapper;
import com.newrelic.instrumentation.labs.vertx.VertxUtils;
import io.vertx.core.Handler;
import io.vertx.core.internal.ContextInternal;

@Weave(type = MatchType.BaseClass, originalName = "io.vertx.core.impl.ContextBase")
abstract class ContextBase_Instrumentation implements ContextInternal {

    @Trace
    public void execute(Runnable task) {
        if(!executor().inThread()) {
            NRRunnableWrapper wrapper = VertxUtils.getRunnableWrapper(task);
            if(wrapper != null) {
                task =  wrapper;
            }
        }
        Weaver.callOriginal();
    }

    @Trace
    public <T> void execute(T argument, Handler<T> task) {
        if(!executor().inThread()) {
            NRTaskWrapper<T> wrapper = VertxUtils.getTaskWrapper(task);
            if(wrapper != null) {
                task = wrapper;
            }
        }
        Weaver.callOriginal();
    }

    public <T> void emit(T argument, Handler<T> task) {
        if(!executor().inThread()) {
            NRTaskWrapper<T> wrapper = VertxUtils.getTaskWrapper(task);
            if(wrapper != null) {
                task = wrapper;
            }
        }
        Weaver.callOriginal();
    }
}