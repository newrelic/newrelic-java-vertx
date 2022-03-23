package com.nr.instrumentation.vertx;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

public class VertxContextHeaders implements Headers {
	
	private HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		List<String> values = attributes.get(name);
		if(values == null) return null;
		if(values.isEmpty()) return null;
		return values.get(0);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		List<String> values = attributes.get(name);
		if(values == null) return Collections.emptyList();
		return values;
	}

	@Override
	public void setHeader(String name, String value) {
		List<String> values = attributes.get(name);
		if(values == null) {
			values = Collections.singletonList(value);
		} else {
			values.add(0, value);
		}
		attributes.put(name, values);
	}

	@Override
	public void addHeader(String name, String value) {
		List<String> values = attributes.get(name);
		if(values == null) {
			values = Collections.singletonList(value);
		} else {
			values.add(value);
		}
		attributes.put(name, values);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return attributes.keySet();
	}

	@Override
	public boolean containsHeader(String name) {
		return getHeaderNames().contains(name);
	}

}
