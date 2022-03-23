package com.nr.fit.instrumentation.vertx.verticles;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class VerticlesClassMethodMatcher implements ClassAndMethodMatcher {
	
	private ClassMatcher classMatcher = new VerticleClassMatcher();
	private MethodMatcher methodMatcher = new VerticleMethodMatcher();

	@Override
	public ClassMatcher getClassMatcher() {
		return classMatcher;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return methodMatcher;
	}

}
