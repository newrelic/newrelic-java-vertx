package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

import io.vertx.core.impl.future.Listener;

public class NRCompletionListener<T> implements Listener<T> {

	private Segment segment = null;
	
	public NRCompletionListener(Segment s) {
		segment = s;
	}
	
	
	@Override
	public void onSuccess(T value) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","NRCompletionListener","onSuccess"});
		if(segment != null) {
			segment.end();
			segment = null;
		}
	}


	@Override
	public void onFailure(Throwable failure) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","NRCompletionListener","onFailure"});
		if(segment != null) {
			segment.end();
			segment = null;
		}
	}

}
