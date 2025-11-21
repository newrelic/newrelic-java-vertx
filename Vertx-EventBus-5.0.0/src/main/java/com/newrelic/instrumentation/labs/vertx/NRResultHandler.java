package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import io.vertx.core.Handler;

public class NRResultHandler<T> implements Handler<T> {

    private NRSegmentHolder holder = null;

    public NRResultHandler(NRSegmentHolder holder) {
        this.holder = holder;
    }

    @Override
    public void handle(T event) {
        holder.end();
        holder = null;
    }
}
