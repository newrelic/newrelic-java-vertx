package com.newrelic.instrumentation.vertx.verticles;

import java.util.Date;
import java.util.Random;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Sender extends AbstractVerticle {
	
	private static Random random = new Random();
	

  @Override
  public void start() throws Exception {

    EventBus eb = vertx.eventBus();

    vertx.runOnContext(v -> {
    	publish(eb);
    });

    vertx.runOnContext(v -> {
    	publish(eb);
    });
    
    
  }
  
  public void publish(EventBus eb) {
	  System.out.println("Publishing news!");
	  Date now = new Date();
  	  eb.send("news-feed", "Some news at "+now+"!",handler());
  }
  
  public Handler<AsyncResult<Message<String>>> handler() {
	  return s -> doStuff(s);
  }
  
  public void doStuff(AsyncResult<Message<String>> s) {
	  int i = random.nextInt(15);
	  try {
		Thread.sleep(i*100L);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
