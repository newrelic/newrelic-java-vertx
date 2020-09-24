package io.vertx.core.net.impl;

import io.netty.channel.ChannelHandlerContext;
import io.vertx.core.impl.ContextImpl;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.BaseClass)
public abstract class VertxHandler<C extends ConnectionBase> {

	@Trace(dispatcher=true)
	public void channelRead(ChannelHandlerContext chctx, Object msg) {
		Weaver.callOriginal();
	}
	
	@Trace
	protected void handleMessage(C connection, ContextImpl context, ChannelHandlerContext chctx, Object msg) {
		Weaver.callOriginal();
	}
}
