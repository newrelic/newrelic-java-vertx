package com.nr.fit.instrumentation.vertx.verticles;

import com.newrelic.agent.MetricNames;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.PointCutClassTransformer;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;

public class VerticlePointcut extends TracerFactoryPointCut {

	public VerticlePointcut(PointCutClassTransformer classTransformer) {
		super(new PointCutConfiguration("verticles"),new VerticleClassMatcher(), new VerticleMethodMatcher());
	}

	@Override
	protected Tracer doGetTracer(Transaction transaction, ClassMethodSignature sig, Object verticle, Object[] args) {
		return new VerticleMethodTracer(transaction, sig, verticle);
	}
	
	private static class VerticleMethodTracer extends OtherRootTracer {
		
		public VerticleMethodTracer(Transaction transaction, ClassMethodSignature sig, Object verticle) {
			super(transaction,sig,verticle, new ClassMethodMetricNameFormat(sig, verticle,MetricNames.OTHER_TRANSACTION+"/Verticle"));
		}
	}
}
