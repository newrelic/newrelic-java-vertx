package com.nr.fit.instrumentation.vertx.verticles;

import java.util.Collection;

import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;

public class VerticleClassMatcher extends ClassMatcher {
	
	private ChildClassMatcher delegate = new ChildClassMatcher("io.vertx.core.AbstractVerticle");

	@Override
	public boolean isMatch(ClassLoader loader, ClassReader cr) {
		return delegate.isMatch(loader, cr);
	}

	@Override
	public boolean isMatch(Class<?> clazz) {
		return delegate.isMatch(clazz);
	}

	@Override
	public Collection<String> getClassNames() {
		return delegate.getClassNames();
	}

}
