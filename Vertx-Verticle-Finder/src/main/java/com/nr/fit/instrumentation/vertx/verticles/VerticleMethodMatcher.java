package com.nr.fit.instrumentation.vertx.verticles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class VerticleMethodMatcher implements MethodMatcher {

	private static final List<String> ignoredMethods;

	static {
		ignoredMethods = new ArrayList<String>();
		ignoredMethods.add("start");
		ignoredMethods.add("stop");
		ignoredMethods.add("rxStart");
		ignoredMethods.add("rxStop");
		ignoredMethods.add("config");
		ignoredMethods.add("deploymentID");
		ignoredMethods.add("getVertx");
		ignoredMethods.add("init");
		ignoredMethods.add("processArgs");
	}

	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations) {
		boolean matches =  !ignoredMethods.contains(name);
		return matches;
	}

	@Override
	public Method[] getExactMethods() {
		return null;
	}

}
