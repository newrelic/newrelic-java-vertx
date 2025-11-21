package com.newrelic.instrumentation.labs.vertx;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;


public class NRSegmentHolder {

    private Segment segment = null;

    public NRSegmentHolder(String segmentName) {
        segment = NewRelic.getAgent().getTransaction().startSegment(segmentName);
    }

    public void end() {
        segment.end();
        segment = null;
    }
}
