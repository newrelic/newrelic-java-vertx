package io.vertx.core.impl.future;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.NRCompletableWrapper;
import com.newrelic.instrumentation.labs.vertx.VertxUtils;
import io.vertx.core.Completable;

@Weave(originalName = "io.vertx.core.impl.future.FutureBase", type = MatchType.BaseClass)
public abstract class FutureBase_Instrumentation<T> {

    public void addListener(Completable<? super T> listener) {
        NRCompletableWrapper<? super T> wrapper = VertxUtils.getCompletableWrapper(listener);
        if (wrapper != null) {
            listener = wrapper;
        }
        Weaver.callOriginal();
    }
}
