package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import io.vertx.core.AsyncResult;
import io.vertx.core.impl.future.Listener;

public class NRFutureListenerWrapper<T> implements Listener<T> {

    private final Listener<T> delegate;
    private Token token;
    private static boolean isTransformed = false;

    public NRFutureListenerWrapper(Listener<T> delegate, Token token) {
        this.delegate = delegate;
        this.token = token;
        if(!isTransformed) {
            AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
            isTransformed = true;
        }
    }

    @Override
    @Trace(async=true)
    public void onSuccess(T value) {
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        delegate.onSuccess(value);
    }

    @Override
    @Trace(async=true)
    public void onFailure(Throwable failure) {
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        delegate.onFailure(failure);
    }

}
