package com.nr.fit.instrumentation.vertx.verticles;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher.Match;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.api.agent.NewRelic;

public class VerticlesClassTransformer implements ContextClassTransformer {

	private ClassMatchVisitorFactory matcher = null;
	
	public ClassMatchVisitorFactory getMatcher() {
		return matcher;
	}
	public VerticlesClassTransformer() {
		matcher = OptimizedClassMatcherBuilder.newBuilder().addClassMethodMatcher(new VerticlesClassMethodMatcher()).build();
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer, InstrumentationContext context, Match match)
			throws IllegalClassFormatException {
		
		for (Method method : match.getMethods()) {
            for (ClassAndMethodMatcher matcher : match.getClassMatches().keySet()) {
                if (matcher.getMethodMatcher().matches(MethodMatcher.UNSPECIFIED_ACCESS, method.getName(),
                        method.getDescriptor(), match.getMethodAnnotations(method))) {
                	NewRelic.getAgent().getLogger().log(Level.FINE, "Instrumenting the Verticle class {0} and method {1}",className,method.getName());
                    context.putTraceAnnotation(method, TraceDetailsBuilder.newBuilder().build());
                }
            }
			
		}
		return null;
	}

}
