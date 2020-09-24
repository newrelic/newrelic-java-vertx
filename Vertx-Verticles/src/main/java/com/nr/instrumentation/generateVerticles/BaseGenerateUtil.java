package com.nr.instrumentation.generateVerticles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.newrelic.agent.Agent;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.ConfigFileHelper;
import com.newrelic.agent.config.ConfigService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.NewRelic;


/*
 * Created this class so it could be used elsewhere
 */

public abstract class BaseGenerateUtil extends Thread {

	
	protected File rootDirectory = null;
	protected File srcDirectory = null;
	protected File compileDirectory = null;
	protected File libDirectory = null;
	protected Set<File> generatedJavaFiles = new HashSet<File>();
	protected Set<File> compiles = new HashSet<File>();
	protected JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	protected String extensionJarName = null;
	
	protected static final List<String> ignoredPackages;
	protected static final List<String> ignoredModifiers;
	protected static final String SRCDIR = "src/main/java";
	protected static final String COMPILEDIR = "build/classes";
	protected static final List<String> RESOURCES;
	protected static final List<String> modifiers;
	protected String tmpPrefix = null;
	protected Set<String> instrumentedClasses = new HashSet<String>();
	
	static {
		ignoredPackages = new ArrayList<String>();
		ignoredPackages.add("java.lang");
		
		ignoredModifiers = new ArrayList<String>();
		ignoredModifiers.add("abstract");
		ignoredModifiers.add("final");
		RESOURCES = new ArrayList<String>();
		RESOURCES.add("agent-bridge.jar");
		RESOURCES.add("newrelic-api.jar");
		RESOURCES.add("newrelic-weaver-api.jar");
		modifiers = new ArrayList<String>();
		modifiers.add("public");
		modifiers.add("protected");
		modifiers.add("private");
		modifiers.add("abstract");
		modifiers.add("final");
		modifiers.add("synchronized");
		modifiers.add("native");
	}
	
	public void init(String prefix) throws IOException {
		tmpPrefix = prefix;
		Path path = Files.createTempDirectory(tmpPrefix);
		rootDirectory = path.toFile();
		cleanUpDirs();
		rootDirectory.deleteOnExit();
		srcDirectory = new File(rootDirectory, SRCDIR);
		srcDirectory.mkdirs();
		compileDirectory = new File(rootDirectory, COMPILEDIR);
		compileDirectory.mkdirs();
		libDirectory = new File(rootDirectory,"lib");
		libDirectory.mkdir();
		getResources();
		Runtime.getRuntime().addShutdownHook(this);
	}
	
	protected final boolean isInstrumented(String className) {
		return instrumentedClasses.contains(className);
	}
	
	protected final void addInstrumented(String className) {
		instrumentedClasses.add(className);
	}
	
	protected static String getAppName() {
		String app_name = "Unknown";
		try {
			app_name = ServiceFactory.getRPMService().getApplicationName();
		} catch (Exception e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to get app name");
		}
		return app_name;
	}
	
	protected abstract String getManifest();

	public final void run() {
		deleteFiles(rootDirectory);
	}
	
	protected final void deleteFiles(File dir) {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				if(!file.isDirectory()) {
					file.delete();
				} else {
					deleteFiles(file);
				}
			}
			dir.delete();
		}
	}
	
	protected final void cleanUpDirs() {
		File parent = rootDirectory.getParentFile();
		String rootdirectoryName = rootDirectory.getName();
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(tmpPrefix);
			}
		};
		File[] files = parent.listFiles(filter);
		for(File file : files) {
			if(file.isDirectory()) {
				if(file.getName().equals(rootdirectoryName)) {
					deleteFiles(file);
				}
			} else {
				file.delete();
			}
		}
		
	}
	
	protected final String getPackageName(String fullclassname) {
		int index = fullclassname.lastIndexOf('.');
		if(index > -1) {
			return fullclassname.substring(0, index);
		}
		return null;
	}

	protected abstract void checkForIgnored(HashMap<String,String> importsMap, String fullClassName);
	
	protected final HashMap<String,String> generateImports(Class<?> proxyInterface) {
		HashMap<String,String> classesToImport = new HashMap<String,String>();
		String packageName = proxyInterface.getPackage().getName();
		
		TypeVariable<?>[] interfaceTypeParams = proxyInterface.getTypeParameters();
		if(interfaceTypeParams.length > 0) {
			for(TypeVariable<?> variable : interfaceTypeParams) {
				String name = variable.getName();
				
				String pn = getPackageName(name);
				if(pn != null) {
					pn = pn.trim();
				}
				if(pn != null && !packageName.equals(pn) && !ignoredPackages.contains(pn)) {
					String classname = getClassName(name);
					classesToImport.put(name,classname);
				}
			}
 		}
		
		Method[] declaredMethods = proxyInterface.getMethods();
		// gather classes needed for import statements
		for(Method method : declaredMethods) {
			Class<?> returnType = method.getReturnType();
			int modifiers = method.getModifiers();
			// don't instrument static methods
			if(Modifier.isStatic(modifiers)) continue;
			
			if(!returnType.isPrimitive()) {
				String returnPackage = returnType.getPackage().getName();
				TypeVariable<?>[] returnTypeParams = returnType.getTypeParameters();
				if(returnTypeParams.length > 0) {
					for(TypeVariable<?> variable : returnTypeParams) {
						String name = variable.getName();
						String pn = getPackageName(name);
						if(pn != null) {
							pn = pn.trim();
						}
						if(pn != null && !packageName.equals(pn) && !ignoredPackages.contains(pn)  && !classesToImport.containsKey(name)) {
							
							classesToImport.put(name,getClassName(name));
						}
					}
				}
				// only add if not already there, 
				if(!returnPackage.equals(packageName)) {
					if(!classesToImport.containsKey(returnType.getName()) && !ignoredPackages.contains(returnPackage)) {
						classesToImport.put(returnType.getName(),returnType.getSimpleName());
					}
				}
			}
			
			Class<?>[] parametypes = method.getParameterTypes();
			for(Class<?> parameterClass : parametypes) {
				if(classesToImport.containsKey(parameterClass.getName())) continue;
				
				if (!parameterClass.isPrimitive()) {
					
					String parmPackage = parameterClass.getPackage().getName();
					if (!parmPackage.equals(packageName) && !ignoredPackages.contains(parmPackage)) {
						classesToImport.put(parameterClass.getName(),parameterClass.getSimpleName());
					}
				}
			}
			
			String genericString = method.toGenericString();
			List<String> genericImports = getImportsFromGenericMethodString(genericString, packageName); 
			for(String newImport : genericImports) {
				
				int index = newImport.lastIndexOf('.');
				if (index > -1) {
					String packName = newImport.substring(0, index);
					if (!packName.equals(packageName)) {
						if (!classesToImport.containsKey(newImport) && !ignoredPackages.contains(packName)) {
							classesToImport.put(newImport,getClassName(newImport));
						} 
					}
				}
			}
		}

		checkForIgnored(classesToImport, proxyInterface.getName());
		return classesToImport;
	}
	
	protected final List<String> getImportsFromGenericMethodString(String generic,String packagename) {
		HashSet<String> imports = new HashSet<String>();
		
		StringTokenizer st1 = new StringTokenizer(generic);
		
		while (st1.hasMoreTokens()) {
			String token1 = st1.nextToken().trim();
			
			if(token1.equalsIgnoreCase("throws")) break;
			
			if(modifiers.contains(token1)) continue;
			
			
			int index = generic.indexOf('(');
			
			if (index > -1) {
				int index2 = generic.indexOf(')', index);
				if (index2 > -1) {
					String paramList = generic.substring(index + 1, index2);
					StringTokenizer st = new StringTokenizer(paramList, ",");
					while (st.hasMoreTokens()) {
						String token = st.nextToken().trim();
						if (token.startsWith("java.lang"))
							continue;
						String pn = getPackageName(token);

						// null package name means primitive
						if (pn == null)
							continue;
						if (pn.equalsIgnoreCase(packagename))
							continue;

						if (!token.contains("<")) {
							// class is not generic
							imports.add(token);
						} else {
							List<String> tmp = getImportsFromGenericClass(token,packagename);
							imports.addAll(tmp);
						}
					}
				}
			} else {
				// assume this is return type
				imports.addAll(getImportsFromGenericClass(token1,packagename));
			}
		}
		return new ArrayList<String>(imports);
	}

	protected final List<String> getImportsFromGenericClass(String genericClass, String packagename) {
		HashSet<String> hashSet = new HashSet<String>();
		int index = genericClass.indexOf('<');
		if(index > -1) {
			int index2 = genericClass.lastIndexOf('>');
			if(index2 > -1) {
				String classname = genericClass.substring(0,index);
				String pn = getPackageName(classname);
				if(!packagename.equalsIgnoreCase(pn)) {
					hashSet.add(classname);
				}
				String embeddedClass = genericClass.substring(index+1, index2);
				if(embeddedClass.contains("<")) {
					hashSet.addAll(getImportsFromGenericClass(embeddedClass,packagename));
				} else {
					hashSet.add(embeddedClass);
				}
			}
		}

		return new ArrayList<String>(hashSet);
	}
	

	
	protected final String getClassName(String c) {
		int index = c.lastIndexOf('.');
		if(index > -1) {
			return c.substring(index+1);
		}
		return c;
	}

	protected final boolean compileJavaFiles() {
		
		List<String> optionList = new ArrayList<String>();
		File nrDirectory = ConfigFileHelper.getNewRelicDirectory();
		if(nrDirectory != null) {

			
			String pathSeparator = System.getProperty("path.separator",":");
			StringBuffer sb = new StringBuffer(System.getProperty("java.class.path"));
			sb.append(pathSeparator);
			File newRelicJar = new File(nrDirectory, "newrelic.jar");
			sb.append(newRelicJar.getAbsolutePath());
			int size = RESOURCES.size();
			int i = 0;
			if(size > 0) {
				sb.append(pathSeparator);
			}
			for(String resource : RESOURCES) {
				i++;
				File resourceFile = new File(libDirectory,resource);
				
				sb.append(resourceFile.getAbsolutePath());
				if(i < size) {
					sb.append(pathSeparator);
				}
			}
			optionList.addAll(Arrays.asList("-classpath",sb.toString()));
		}

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(generatedJavaFiles);
		StringWriter sWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(sWriter);
		Boolean result = compiler.getTask(writer, fileManager, null, optionList, null, compilationUnits).call();
		String errString = sWriter.toString();
		if(errString != null && !errString.isEmpty()) {
			NewRelic.getAgent().getLogger().log(Level.FINE, "Output from compile: {0}", errString);
		}
		try {
			fileManager.close();
		} catch (IOException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to close filemanager");
		}
		return result;
	}
	
	protected final Path compileJavaFile(File javaFile) {
		String filename = javaFile.getName().replace(".java", ".class");
		
		String path = javaFile.getAbsolutePath();
		int result = compiler.run(null, null, null, path);
		NewRelic.getAgent().getLogger().log(Level.FINE, "Result of compiling file {0} was {1}", javaFile.getAbsolutePath(),result);
		if(result == 0) {
			return javaFile.toPath().getParent().resolve(filename);
		}
		return null;
	}
	
	protected final void getResources() {
		File nrDirectory = ConfigFileHelper.getNewRelicDirectory();
		if(nrDirectory == null) {
			return ;
		}
		File newRelicJar = new File(nrDirectory, "newrelic.jar");
		
		
		try {
			JarFile nrJar = new JarFile(newRelicJar);
			Enumeration<JarEntry> entries = nrJar.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if(entry.isDirectory()) {
					File dest = new File(libDirectory,entry.getName());
					dest.mkdirs();
					continue;
				}
				if(entry == null || (!entry.getName().endsWith(".jar"))) continue;
				if(entry != null) {
					InputStream in = nrJar.getInputStream(entry);
					File outFile = new File(libDirectory,entry.getName());
					FileOutputStream out = new FileOutputStream(outFile);
					int size = 4098;
					byte[] buffer = new byte[size];
					int read = 0;
					while((read = in.read(buffer)) != -1) {
						out.write(buffer, 0, read);
					}
					out.close();
				}
			}
			nrJar.close();
		} catch (IOException e) {

		}
		
		
	}
	
	protected static final File getExtensionsDirectory() {
		
		if(ServiceFactory.getServiceManager() == null) {
			return null;
		}
		ConfigService configService = ServiceFactory.getConfigService();
		AgentConfig agentConfig = configService.getDefaultAgentConfig();
		String configDirName = (String) agentConfig.getProperty("extensions.dir");
		if (configDirName == null) {
			configDirName = ConfigFileHelper.getNewRelicDirectory() + File.separator + "extensions";
		}

		File configDir = new File(configDirName);
		if (!configDir.exists()) {
			Agent.LOG.log(Level.FINE, "The extension directory " + configDir.getAbsolutePath() + " does not exist.");
			configDir = null;
		} else if (!configDir.isDirectory()) {
			Agent.LOG.log(Level.WARNING,
					"The extension directory " + configDir.getAbsolutePath() + " is not a directory.");
			configDir = null;
		} else if (!configDir.canRead()) {
			Agent.LOG.log(Level.WARNING,
					"The extension directory " + configDir.getAbsolutePath() + " is not readable.");
			configDir = null;
		}

		return configDir;
	}

	protected final void copyClassFiles() {
		deleteFiles(compileDirectory);
		for(File generated : generatedJavaFiles) {
			String filename = generated.getAbsolutePath().replace(".java", ".class");
			String copyTo = filename.replace(SRCDIR, COMPILEDIR);
			Path actual = Paths.get(filename);
			Path copyToPath = Paths.get(copyTo);
			copyToPath.getParent().toFile().mkdirs();
			try {
				Path copied = Files.copy(actual, copyToPath);
				compiles.add(copied.toFile());
			} catch (IOException e) {
				NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to copy {0} to {1}",filename,copyTo);
			}
			
		}
	}
	
	protected final void createJar() throws IOException {
		InputStream in = new ByteArrayInputStream(getManifest().getBytes());
		Manifest manifest = new Manifest(in);
		File extensionsDirectory = getExtensionsDirectory();
		File jarfile = null;
		if(extensionsDirectory != null) {
			jarfile = new File(extensionsDirectory,extensionJarName);
		} else {
			jarfile = new File(extensionJarName);
		}
		OutputStream out = new FileOutputStream(jarfile);
		JarOutputStream jarout = new JarOutputStream(out, manifest);
		
		for(File compiledClass : compiles) {
			String filename = compiledClass.getAbsolutePath();
			int index = filename.indexOf("build/classes/");
			String tmp = filename.substring(index+"build/classes/".length());
			JarEntry je = new JarEntry(tmp);
			je.setTime(compiledClass.lastModified());
			jarout.putNextEntry(je);
			FileInputStream input = new FileInputStream(compiledClass);
			byte[] newBytes = new byte[4096];
			int size = input.read(newBytes);
			while(size != -1) {
				jarout.write(newBytes, 0, size);
				size = input.read(newBytes);
			}
			input.close();
		}
		jarout.close();
	}
	
	
	protected abstract String modifyGeneric(String generic, String packageName, String fullclassname, HashMap<String,String> classesToImport);
}
