package com.nr.instrumentation.vertx;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;

import io.vertx.core.MultiMap;

public class OutboundMessageWrapper implements OutboundHeaders {
	
	private final MultiMap headers = null;

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String name, String value) {
		
	}

}
