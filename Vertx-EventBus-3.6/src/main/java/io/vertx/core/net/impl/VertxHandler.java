package io.vertx.core.net.impl;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import io.netty.channel.ChannelHandlerContext;

@Weave(type=MatchType.BaseClass)
public abstract class VertxHandler<C extends ConnectionBase> {

	@Trace(dispatcher=true)
	public void channelRead(ChannelHandlerContext chctx, Object msg) {
		Weaver.callOriginal();
	}
	
}
