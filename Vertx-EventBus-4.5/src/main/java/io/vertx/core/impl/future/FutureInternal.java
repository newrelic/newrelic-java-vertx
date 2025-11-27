package io.vertx.core.impl.future;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.NRFutureListenerWrapper;
import com.newrelic.instrumentation.labs.vertx.VertxUtils;

@Weave(type = MatchType.Interface)
public class FutureInternal<T> {

    public void addListener(Listener<T> listener) {
        if(listener != null) {
            NRFutureListenerWrapper<T> wrapper = VertxUtils.getListenerWrapper(listener);
            if(wrapper != null) {
                listener = wrapper;
            }
        }
        Weaver.callOriginal();
    }
}
