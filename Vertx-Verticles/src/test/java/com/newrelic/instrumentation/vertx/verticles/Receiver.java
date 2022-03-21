package com.newrelic.instrumentation.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Receiver extends AbstractVerticle {

	private Handler<Future<Boolean>> handler;
	private int count = 0;

	public Receiver(Handler<Future<Boolean>> h) {
		handler = h;
	}
	
  @Override
  public void start() throws Exception {

    EventBus eb = vertx.eventBus();

    dispatch(eb, 1);
    dispatch(eb, 2);
    dispatch(eb, 3);
    System.out.println("Ready!");
  }
  
  public void dispatch(EventBus eb,int i) {
	    MessageConsumer<?> consumer1 = eb.consumer("news-feed", handle(i));
	    HandlerVerticle handler1 = new HandlerVerticle(consumer1);
	    vertx.deployVerticle(handler1);
  }
  
  public Handler<Message<Object>> handle(int i) {
	  return message -> recordMessage(message,i);
  }
  
  public void recordMessage(Message<Object> msg, int i) {
	  count++;
	  System.out.println("Received news on consumer "+i+": " + msg.body());
	  try {
		Thread.sleep(1200L);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		if(count > 1) {
	    	if(handler != null) {
	        	handler.handle(Future.succeededFuture(true));
	        }
		}

  }
}
