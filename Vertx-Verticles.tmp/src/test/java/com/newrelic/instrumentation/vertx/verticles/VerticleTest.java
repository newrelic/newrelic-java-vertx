package com.newrelic.instrumentation.vertx.verticles;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.InstrumentationTestRunner;

import io.vertx.core.Vertx;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = { "io.vertx" })
public class VerticleTest {
	
	@BeforeClass
	public static void beforeClass() {
	}

	@Test
	public void test() {
		Vertx vertx = Vertx.vertx();
		Receiver receiver = new Receiver();
		Sender sender = new Sender();
//		vertx.deployVerticle(receiver);
//		vertx.deployVerticle(sender);
		
		
	}
}
