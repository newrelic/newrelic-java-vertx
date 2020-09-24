package io.vertx.core.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.vertx.NRCompletionWrapper;
import com.nr.instrumentation.vertx.NRTaskWrapper;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@SuppressWarnings("unchecked")
@Weave
abstract class FutureImpl<T> {

    private Handler<AsyncResult<T>> handler = Weaver.callOriginal();

    public abstract boolean isComplete();

	@Trace(async = true, excludeFromTransactionTrace = true)
    public Future<T> setHandler(Handler<AsyncResult<T>> theHandler) {
    	if(isComplete()) {
    		if(NRTaskWrapper.class.isInstance(theHandler)) {
    			((NRTaskWrapper<T>)theHandler).linkAndExpireToken();
    		}
    	}
        if (theHandler != null && !isComplete() && !NRCompletionWrapper.class.isInstance(theHandler)) {
        	NRTaskWrapper<AsyncResult<T>> wrapper = new NRTaskWrapper<AsyncResult<T>>(theHandler, NewRelic.getAgent().getTransaction().getToken());
            theHandler = wrapper;
        }
        return Weaver.callOriginal();
    }

    @Trace(async = true, excludeFromTransactionTrace = true)
    public boolean tryComplete(Object result) {
		if(NRTaskWrapper.class.isInstance(handler)) {
			((NRTaskWrapper<T>)handler).linkAndExpireToken();
		}
        return Weaver.callOriginal();
    }

	@Trace(async = true, excludeFromTransactionTrace = true)
    public boolean tryFail(Throwable cause) {
		if(NRTaskWrapper.class.isInstance(handler)) {
			((NRTaskWrapper<T>)handler).linkAndExpireToken();
		}
        return Weaver.callOriginal();
    }
}