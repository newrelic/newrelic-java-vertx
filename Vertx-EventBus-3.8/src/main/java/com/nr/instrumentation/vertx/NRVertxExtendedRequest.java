package com.nr.instrumentation.vertx;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;

import com.newrelic.api.agent.ExtendedRequest;
import com.newrelic.api.agent.HeaderType;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;

public class NRVertxExtendedRequest extends ExtendedRequest {
	
	private HttpServerRequest request = null;
	
	public NRVertxExtendedRequest(HttpServerRequest req) {
		request = req;
	}

	@Override
	public String getRequestURI() {
		return request.absoluteURI();
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public Enumeration getParameterNames() {
		MultiMap params = request.params();
		
		return Collections.enumeration(params.names());
	}

	@Override
	public String[] getParameterValues(String name) {
		MultiMap params = request.params();
		
		List<String> values = params.getAll(name);
		
		String[] array = new String[values.size()];
		
		return values.toArray(array);
	}

	@Override
	public Object getAttribute(String name) {
		return request.formAttributes().get(name);
	}

	@Override
	public String getCookieValue(String name) {
		return null;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	@Override
	public String getMethod() {
		return request.method().name();
	}

}
