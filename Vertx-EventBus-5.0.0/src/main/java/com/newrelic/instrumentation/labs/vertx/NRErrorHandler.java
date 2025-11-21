package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import io.vertx.core.Handler;

public class NRErrorHandler implements Handler<Throwable> {

    private NRSegmentHolder holder = null;

    public NRErrorHandler(NRSegmentHolder holder) {
       this.holder = holder;
    }

    @Override
    public void handle(Throwable event) {
        NewRelic.noticeError(event);
        holder.end();
        holder = null;
    }
}
