package com.newrelic.instrumentation.vertx.verticles;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;

import io.vertx.core.Verticle;

public class VerticleUtils {

	private static final List<String> ignoredMethods;
	private static final List<Method> instrumented = new ArrayList<Method>();
	
	

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

	public static void instrument(Verticle... verticles) {
		for(Verticle verticle : verticles) {
			NewRelic.getAgent().getLogger().log(Level.FINE, "Instrumenting Verticle {0}", verticle);
			Class<?> verticleClass = verticle.getClass();
			Method[] methods = verticleClass.getDeclaredMethods();
			for(Method method : methods) {
				if (instrumented.contains(method)) {
					String methodName = method.getName();
					if (!ignoredMethods.contains(methodName)) {
						int modifiers = method.getModifiers();
						// don't instrument private methods
						if (!Modifier.isPrivate(modifiers)) {
							AgentBridge.instrumentation.instrument(method, "Vertx/Verticle");
							instrumented.add(method);
						}
					} 
				}
			}
		}
		
	}
}
