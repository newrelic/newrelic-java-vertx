package com.newrelic.instrumentation.labs.vertx;

import java.util.Collection;
import java.util.Collections;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;

public class DeliveryOptionsHeaders implements Headers {
	
	private DeliveryOptions options = null;;
	
	public DeliveryOptionsHeaders(DeliveryOptions o) {
		options = o;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		MultiMap headers = options.getHeaders();
		if(headers == null) return null;
		
		return headers.get(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		MultiMap headers = options.getHeaders();
		if(headers == null) return Collections.emptyList();
		return headers.getAll(name);
	}

	@Override
	public void setHeader(String name, String value) {
		options.addHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		options.addHeader(name, value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		MultiMap headers = options.getHeaders();
		if(headers == null) return Collections.emptyList();
		return headers.names();
	}

	@Override
	public boolean containsHeader(String name) {
		MultiMap headers = options.getHeaders();
		if(headers == null) return false;
		return headers.contains(name);
	}

}
