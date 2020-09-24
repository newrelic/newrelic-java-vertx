package com.nr.instrumentation.generateVerticles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.newrelic.api.agent.NewRelic;

import io.vertx.core.Verticle;

public class VerticleInstrumentation extends BaseGenerateUtil {

	private static final List<String> ignoredMethods;

	protected static final String handlerDirectory = "com/nr/instrumentation/vertx/verticles";

	private static final String handlerCode = "package com.nr.instrumentation.vertx.verticles;\n" + 
			"\n" + 
			"import com.newrelic.agent.bridge.AgentBridge;\n" + 
			"import com.newrelic.api.agent.Token;\n" + 
			"import com.newrelic.api.agent.Trace;\n" + 
			"\n" + 
			"import io.vertx.core.AsyncResult;\n" + 
			"import io.vertx.core.Handler;\n" + 
			"\n" + 
			"public class NRHandlerWrapper<T> implements Handler<AsyncResult<T>> {\n" + 
			"	\n" + 
			"	private Handler<AsyncResult<T>> delegate = null;\n" + 
			"	private Token token = null;\n" + 
			"	private static boolean isTransformed = false;\n" + 
			"	\n" + 
			"	public NRHandlerWrapper(Handler<AsyncResult<T>> d, Token t) {\n" + 
			"		delegate = d;\n" + 
			"		token = t;\n" + 
			"		if(!isTransformed) {\n" + 
			"			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());\n" + 
			"			isTransformed = true;\n" + 
			"		}\n" + 
			"	}\n" + 
			"\n" + 
			"	@Override\n" + 
			"	@Trace(async=true,excludeFromTransactionTrace=true)\n" + 
			"	public void handle(AsyncResult<T> event) {\n" + 
			"		if(token != null) {\n" + 
			"			token.linkAndExpire();\n" + 
			"			token = null;\n" + 
			"		}\n" + 
			"		delegate.handle(event);\n" + 
			"	}\n" + 
			"\n" + 
			"}\n" + 
			"";

	protected static VerticleInstrumentation instance = null;

	protected static final String MANIFEST = "Manifest-Version: 1.0\n" + 
			"Implementation-Title: com.newrelic.instrumentation.Vertx-Verticles-Custom\n" + 
			"Implementation-Version: 1.0\n" + 
			"Implementation-Vendor-Id: Field Instrumentation\n" + 
			"Implementation-Vendor: New Relic\n" + 
			"\n" + 
			"";

	private static final String NRImports = "import com.newrelic.api.agent.NewRelic;\n" + 
			"import com.newrelic.api.agent.Trace;\n" + 
			"import com.newrelic.api.agent.Token;\n" + 
			"import com.newrelic.api.agent.weaver.Weave;\n" + 
			"import com.newrelic.api.agent.weaver.Weaver;\n" + 
			"import com.nr.instrumentation.vertx.verticles.NRHandlerWrapper;\n";


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


		instance = new VerticleInstrumentation();
		try {
			instance.init();
		} catch (IOException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to initialize VerticleInstrumentation instance");
		}
	}

	// used to instrument a collection of Verticle classes

	public static void generateVerticleWeavers(Verticle...verticles) {
		boolean check = false;
		for(Verticle verticle : verticles) {
			if(!instance.instrument(verticle.getClass())) {
				NewRelic.getAgent().getLogger().log(Level.FINER, "Skipping instrumenting verticle, no methods to instrument: {0}", verticle.getClass().getName());
				continue;
			}
			try {
				if(!instance.isInstrumented(verticle.getClass().getName())) {
					NewRelic.getAgent().getLogger().log(Level.FINER, "Attempting to instrument verticle: {0}", verticle.getClass().getName());
					File generated = instance.generate(verticle.getClass());
					check = true;
					instance.generatedJavaFiles.add(generated);
				} else {
					NewRelic.getAgent().getLogger().log(Level.FINER, "Skipping instrumenting verticle, already instrumented: {0}", verticle.getClass().getName());
				}
				
				if(check) {
					Boolean result = instance.compileJavaFiles();
					NewRelic.getAgent().getLogger().log(Level.FINE, "Result of compiling Vertx Verticle generated weaver classes is {0}", result);
					instance.copyClassFiles();
					
					instance.createJar();
				}
			} catch (IOException e) {
				NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to create Java file for {0}", verticle.getClass());
			}
		}
	}

	// used to instrument a single Verticle class
	public static void generateVerticleWeaver(Verticle verticle) {
		if(!instance.instrument(verticle.getClass())) return;
		if(instance.isInstrumented(verticle.getClass().getName())) {
			NewRelic.getAgent().getLogger().log(Level.FINER, "Skipping instrumenting verticle, already instrumented: {0}", verticle.getClass().getName());
			return;
		}
		try {
			NewRelic.getAgent().getLogger().log(Level.FINER, "Attempting to instrument verticle: {0}", verticle.getClass().getName());
			File generated = instance.generate(verticle.getClass());
			instance.generatedJavaFiles.add(generated);
			
			Boolean result = instance.compileJavaFiles();
			NewRelic.getAgent().getLogger().log(Level.FINE, "Result of compiling Vertx Verticle generated weaver classes is {0}", result);
			instance.copyClassFiles();
			
			instance.createJar();
		} catch (IOException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to create Weaver for {0}", verticle.getClass());
		}
	}

	protected File generateHandler() throws IOException {
		File packageDirectory = new File(srcDirectory,handlerDirectory);
		if(!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}
		
		File handlerJavaFile = new File(packageDirectory,"NRHandlerWrapper.java");
		PrintWriter writer = new PrintWriter(handlerJavaFile);
		writer.print(handlerCode);
		writer.close();
		
		return handlerJavaFile;
	}

	protected String getAddHandlerCode(String cn) {
		StringBuffer code = new StringBuffer();

		code.append("\t\tToken token = NewRelic.getAgent().getTransaction().getToken();\n");
		code.append("\t\tNRHandlerWrapper<"+cn+"> wrapper = ");
		code.append("new NRHandlerWrapper<"+cn+">(handler,token);\n");
		code.append("\t\thandler = wrapper;\n");
		return code.toString();
	}

	/*
	 * Only instrument if the class contains a non-ignored method
	 */
	protected boolean instrument(Class<?> verticleClass) {
		boolean instrumentClass = false;
		
		Method[] methods = verticleClass.getDeclaredMethods();
		for(Method method : methods) {
			if(!ignoredMethods.contains(method.getName())) {
				instrumentClass = true;
				break;
			}
		}
		
		return instrumentClass;
	}

	protected File generate(Class<?> verticleClass) throws FileNotFoundException {

		Method[] declaredMethods = verticleClass.getDeclaredMethods();

		String packageName = verticleClass.getPackage().getName();
		String className = verticleClass.getSimpleName();
		String packageDir = packageName.replace('.',File.separatorChar);
		File packageDirFile = new File(srcDirectory,packageDir);
		if(!packageDirFile.exists()) {
			boolean b = packageDirFile.mkdirs();
			if(!b) {
				System.out.println("Failed to create directory "+packageDirFile.getName());
			}
		}

		File weaverFile = new File(packageDirFile, className+".java");
		PrintWriter writer = new PrintWriter(weaverFile);

		writer.print("package "+packageName+";");
		writer.println();

		HashMap<String,String> classesToImport = generateImports(verticleClass);
		// produce import statements

		for(String importClass : classesToImport.keySet()) {
			writer.println("import "+importClass+";");
		}

		writer.println(NRImports);

		writer.println();

		writer.println("@Weave");
		int interfaceModifiers = verticleClass.getModifiers();
		if(Modifier.isPublic(interfaceModifiers)) {
			writer.print("public ");
		} 
		writer.println("abstract class "+className + "{");

		writer.println();

		for(Method method : declaredMethods) {
			int methodMods = method.getModifiers();
			if(Modifier.isStatic(methodMods) || Modifier.isPrivate(methodMods)) continue;
			if(ignoredMethods.contains(method.getName())) continue;
			writer.println("\t@Trace");

			String genericString = method.toGenericString();
			boolean voidReturn = genericString.contains(" void ");
			String modGeneric = modifyGeneric(genericString,packageName,verticleClass.getName(),classesToImport);
			writer.println("\t"+modGeneric+" {");
			boolean hasHandler = modGeneric.contains("Handler<AsyncResult");
			if(hasHandler) {
				int index = modGeneric.indexOf("Handler<AsyncResult<");
				if(index > -1) {
					int index2 = modGeneric.indexOf("> handler", index);
					if(index2 > -1) {
						String cn = modGeneric.substring(index+"Handler<AsyncResult<".length(), index2-1);
						writer.println(getAddHandlerCode(cn));
					}
				}
			}
			if(voidReturn) {
				writer.println("\t\tWeaver.callOriginal();");
			} else {
				writer.println("\t\treturn Weaver.callOriginal();");
			}
			writer.println("\t}");
			writer.println();

		}

		writer.println("}");
		writer.close();
		return weaverFile;

	}

	protected VerticleInstrumentation() {
	}

	protected void init() throws IOException {
		String name = getAppName() + "-NewRelic-Verticles";
		extensionJarName = name + ".jar";
		super.init(name);
		File handlerJavaFile = generateHandler();
		generatedJavaFiles.add(handlerJavaFile);
	}

	@Override
	protected String getManifest() {
		return MANIFEST;
	}

	@Override
	protected void checkForIgnored(HashMap<String, String> importsMap, String fullClassName) {


	}

	@Override
	protected String modifyGeneric(String generic, String packageName, String fullclassname, HashMap<String, String> classesToImport) {
		String tmp = generic.trim();
		for(String key : classesToImport.keySet()) {
			tmp = tmp.replace(key, classesToImport.get(key));
		}
		for(String pn : ignoredPackages) {
			tmp = tmp.replace(pn+".", "");
		}
		tmp = tmp.replace(packageName+".", "");

		StringTokenizer st = new StringTokenizer(tmp);
		StringBuffer sb = new StringBuffer();
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			// if contains parns it's the method signature
			if(token.contains("(") && token.contains(")")) {
				int index = token.indexOf('(');
				if(index > -1) {
					String tmp2 = token.substring(0, index);
					String methodName = getClassName(tmp2);
					sb.append(methodName);
					sb.append('(');
				}
				int index2 = token.indexOf(')');
				// this should be the comma separated list of parameters
				String parmList = token.substring(index+1,index2);
				StringTokenizer st3 = new StringTokenizer(parmList, ",");
				int paramCount = 0;
				int tokenCount = st3.countTokens();
				while(st3.hasMoreTokens()) {
					paramCount++;
					token = st3.nextToken();
					boolean isHandler = false;
					sb.append(token);
					isHandler = token.startsWith("Handler<AsyncResult");


					if(isHandler) {
						sb.append(" handler");
					} else {
						sb.append(" param"+paramCount);
					}
					if(paramCount < tokenCount) {
						sb.append(',');
					}
				}
				sb.append(") ");
			} else if(classesToImport.containsKey(token)) {
				String classname = getClassName(token);
				sb.append(classname+ " ");
			} else if(!ignoredModifiers.contains(token)){
				int index = token.indexOf('.');
				if(index > -1) {
					sb.append(getClassName(token)+" ");
				} else {
					sb.append(token);
				}
				sb.append(' ');
			}

		}
		return sb.toString();
	}

	
	public static void main(String[] args) {
	}
}
