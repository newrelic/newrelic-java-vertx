package io.vertx.core.eventbus.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import io.vertx.core.eventbus.Message;
import io.vertx.core.internal.ContextInternal;

import java.util.logging.Level;

@Weave(originalName = "io.vertx.core.eventbus.impl.ReplyHandler")
class ReplyHandler_Instrumentation<T> {

    @Trace(async=true)
    protected void doReceive(Message<T> reply) {
        if(reply instanceof MessageImpl_Instrumentation) {
            MessageImpl_Instrumentation msg = (MessageImpl_Instrumentation) reply;
            if(msg.token != null) {
                msg.token.linkAndExpire();
                msg.token = null;
            }
        }
        Weaver.callOriginal();
    }
}
