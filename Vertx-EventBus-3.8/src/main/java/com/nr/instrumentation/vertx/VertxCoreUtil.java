package com.nr.instrumentation.vertx;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.instrumentation.ClassTransformerService;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.instrumentation.weaver.ClassWeaverService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
//import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
//import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.weave.weavepackage.WeavePackage;
import com.newrelic.weave.weavepackage.WeavePackageManager;

//import io.vertx.core.Handler;
import io.vertx.core.http.impl.HttpClientResponseImpl;
//import java.util.Map;

public class VertxCoreUtil {

	public static boolean initialized = false;
	
	private static final String VERTX_CORE_3_6 = "com.newrelic.instrumentation.vertx-core-3.6.0";

	private static final String VERTX_CORE_3_8 = "com.newrelic.instrumentation.vertx-core-3.8.0";

	private static final String VERTX_CORE_3_9 = "com.newrelic.instrumentation.vertx-core-3.9.0";

	private VertxCoreUtil() {
    }

//    private static final Map<Object, Token> tokenMap = AgentBridge.collectionFactory.createConcurrentWeakKeyedMap();

    public static final String VERTX_CLIENT = "Vertx-Client";
    public static final String END = "end";

    private static URI UNKNOWN_HOST_URI = URI.create("http://UnknownHost/");

	private static WeavePackage vertxCorePkg = null;
	
//    public static void storeToken(Handler handler) {
//        if (handler != null && AgentBridge.getAgent().getTransaction(false) != null) {
//            tokenMap.put(handler, NewRelic.getAgent().getTransaction().getToken());
//        }
//    }
//
//    public static void linkAndExpireToken(Handler handler) {
//        if (handler != null) {
//            final Token token = tokenMap.remove(handler);
//            if (token != null) {
//                token.linkAndExpire();
//            }
//        }
//    }
    
	public static void init() {
		Logger logger = NewRelic.getAgent().getLogger();
		logger.log(Level.FINE, "Initializing VertxCoreUtil");
			disable();
		initialized = true;
	}


    public static void processResponse(Segment segment, HttpClientResponseImpl resp, String host, int port,
            String scheme) {
        try {
            URI uri = new URI(scheme, null, host, port, null, null, null);
            segment.reportAsExternal(HttpParameters.library(VERTX_CLIENT)
                                                   .uri(uri)
                                                   .procedure(END)
                                                   .inboundHeaders(new InboundWrapper(resp))
                                                   .build());
        } catch (URISyntaxException e) {
            AgentBridge.instrumentation.noticeInstrumentationError(e, Weaver.getImplementationTitle());
        }
    }

    public static void reportUnknownHost(Segment segment) {
            segment.reportAsExternal(GenericParameters.library(VERTX_CLIENT)
                                                      .uri(UNKNOWN_HOST_URI)
                                                      .procedure(END)
                                                      .build());
    }

	private static void disable() {
		Logger logger = NewRelic.getAgent().getLogger();
		logger.log(Level.FINE, "Call to Utils.disable");
		ClassTransformerService classTransformerService = ServiceFactory.getClassTransformerService();
		if(classTransformerService != null) {
			InstrumentationContextManager ctxMgr = classTransformerService.getContextManager();
			if(ctxMgr != null) {
				ClassWeaverService classWeaverService = ctxMgr.getClassWeaverService();
				if(classWeaverService != null) {
					WeavePackageManager weavePkgMgr = classWeaverService.getWeavePackageManger();
					if(weavePkgMgr != null) {
						WeavePackage weavePackage = weavePkgMgr.deregister(VERTX_CORE_3_6);
						if(weavePackage != null) {
							vertxCorePkg = weavePackage;
							logger.log(Level.FINE, "Disable Weaver package: {0}",VERTX_CORE_3_6);
						} else {
							logger.log(Level.FINE, "Failed to disable weaver package {0} because it was not found",VERTX_CORE_3_6);
						}
						WeavePackage weavePackage2 = weavePkgMgr.deregister(VERTX_CORE_3_8);
						if(weavePackage2 != null) {
							vertxCorePkg = weavePackage2;
							logger.log(Level.FINE, "Disable Weaver package: {0}",VERTX_CORE_3_8);
						} else {
							logger.log(Level.FINE, "Failed to disable weaver package {0} because it was not found",VERTX_CORE_3_8);
						}
						WeavePackage weavePackage3 = weavePkgMgr.deregister(VERTX_CORE_3_9);
						if(weavePackage3 != null) {
							vertxCorePkg = weavePackage3;
							logger.log(Level.FINE, "Disable Weaver package: {0}",VERTX_CORE_3_9);
						} else {
							logger.log(Level.FINE, "Failed to disable weaver package {0} because it was not found",VERTX_CORE_3_9);
						}
					} else {
						logger.log(Level.FINE, "WeavePackageManager is null");
					}
				} else {
					logger.log(Level.FINE, "ClassWeaverService is null");
				}
			} else {
				logger.log(Level.FINE, "InstrumentationContextManager is null");
			}
		} else {
			logger.log(Level.FINE, "ClassTransformerService is null");
		}
	}
}