package com.newrelic.instrumentation.labs.vertx;

import java.util.Collection;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

import io.vertx.core.MultiMap;
import io.vertx.core.http.impl.headers.HeadersMultiMap;

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
		if(headers != null) {
			headers = headers.set(name, value);
		} else {
			headers = new HeadersMultiMap();
			headers.add(name, value);
		}
	}

	@Override
	public void addHeader(String name, String value) {
		headers = headers.add(name, value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.names();
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.contains(name);
	}

	public MultiMap getMultimap() {
		return headers;
	}
}
