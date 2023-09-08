package com.newrelic.instrumentation.labs.vertx;

import java.util.Collection;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

import io.vertx.core.MultiMap;

public class MessageHeaders implements Headers {
	
	MultiMap headers = null;
	
	public MessageHeaders(MultiMap h) {
		headers = h;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return headers.getAll(name);
	}

	@Override
	public void setHeader(String name, String value) {
		headers.set(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		headers.add(name, value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.names();
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.contains(name);
	}

}
