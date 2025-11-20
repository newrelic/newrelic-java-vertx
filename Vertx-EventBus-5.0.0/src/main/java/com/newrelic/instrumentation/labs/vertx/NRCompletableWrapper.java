package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import io.vertx.core.Completable;

public class NRCompletableWrapper<T> implements Completable<T> {

    private final Completable<T> delegate;
    private Token token;
    private static boolean isTransformed = false;

    public NRCompletableWrapper(Completable<T> delegate, Token token) {
        this.delegate = delegate;
        this.token = token;
        if(!isTransformed) {
            isTransformed = true;
            AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
        }
    }

    @Override
    @Trace(async=true)
    public void succeed(T result) {
        NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Completable",delegate.getClass().getName(),"succeed"});
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
       delegate.succeed(result);
    }

    @Override
    @Trace(async=true)
    public void succeed() {
        NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Completable",delegate.getClass().getName(),"succeed"});
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        delegate.succeed();
    }

    @Override
    @Trace(async=true)
    public void fail(Throwable failure) {
        NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Completable",delegate.getClass().getName(),"fail"});
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        NewRelic.noticeError(failure);
        delegate.fail(failure);
    }

    @Override
    @Trace(async=true)
    public void fail(String message) {
        NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Completable",delegate.getClass().getName(),"fail"});
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        NewRelic.noticeError(message);
        delegate.fail(message);
    }

    @Override
    @Trace(async=true)
    public void complete(T result, Throwable failure) {
        NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Completable",delegate.getClass().getName(),"complete"});
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        delegate.complete(result, failure);
    }
}
