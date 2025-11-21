package io.vertx.core.internal;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import io.vertx.core.Handler;

@Weave(originalName = "io.vertx.core.internal.VertxInternal",type = MatchType.Interface)
public class VertxInternal_Instrumentation {

    @Trace(dispatcher=true)
    public void runOnContext(Handler<Void> task) {
            Weaver.callOriginal();
    }

}
