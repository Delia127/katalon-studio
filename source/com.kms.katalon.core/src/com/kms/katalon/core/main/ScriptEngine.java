package com.kms.katalon.core.main;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;

public class ScriptEngine extends GroovyScriptEngine {
	// Used to generate new temp script name
	private int counter;

	private Map<String, Script> scriptLookup;

	public static ScriptEngine getDefault(ClassLoader parentClassLoader) throws IOException {
		URL[] roots = new URL[] { new File(RunConfiguration.getProjectDir(), StringConstants.CUSTOM_KEYWORD_FOLDER_NAME)
				.toURI().toURL() };
		return new ScriptEngine(roots, parentClassLoader);
	}

	public ScriptEngine(URL[] roots, ClassLoader parentClassLoader) {
		super(roots, parentClassLoader);
		counter = 0;
		scriptLookup = new HashMap<String, Script>();
	}

	protected synchronized String generateScriptName() {
		return "Script" + (++counter) + "." + StringConstants.SCRIPT_FILE_EXT;
	}

	// Parse this temporary class without caching
	public Object runScript(final String scriptText, Binding binding)
			throws ResourceException, ScriptException, IOException, ClassNotFoundException {
		return run(getGroovyCodeSource(scriptText, generateScriptName()), binding, false);
	}

	// Parse this temporary class without caching and not logging
	public Object runScriptWithoutLogging(final String scriptText, Binding binding)
			throws ResourceException, ScriptException, IOException, ClassNotFoundException {
		GroovyClassLoader classLoader = null;
		try {
			classLoader = new GroovyClassLoader(getParentClassLoader(),
					TestCaseExecutor.getConfigForCollectingVariable());
			Class<?> clazz = classLoader.parseClass(getGroovyCodeSource(scriptText, generateScriptName()), false);
			return getScript(clazz, binding, false).run();
		} finally {
			if (classLoader != null) {
				classLoader.close();
			}
		}
	}

	// Parse this class as script text
	public Object runScriptAsRawText(final String scriptText, String className, Binding binding)
			throws ResourceException, ScriptException, IOException, ClassNotFoundException {
		String processedScriptText = preProcessScriptBeforeBuild(scriptText);
		return run(getGroovyCodeSource(processedScriptText, className), binding, true);
	}

	public Object runScriptMethodAsRawText(final String scriptText, final String className, final String methodName,
			Object args, Binding binding)
			throws ResourceException, ScriptException, ClassNotFoundException, IOException {
		String processedScriptText = preProcessScriptBeforeBuild(scriptText);
		return getScript(getGroovyCodeSource(processedScriptText, className), binding, true).invokeMethod(methodName,
				args);
	}

	public Object runScriptMethodAsRawText(final String scriptText, final String className, final String methodName,
			Binding binding) throws ResourceException, ScriptException, ClassNotFoundException, IOException {
		String processedScriptText = preProcessScriptBeforeBuild(scriptText);
		return getScript(getGroovyCodeSource(processedScriptText, className), binding, true).invokeMethod(methodName,
				null);
	}

	private String preProcessScriptBeforeBuild(String scriptText) {
		return processNotRunLabels(scriptText);
	}

	private static String processNotRunLabels(String scriptText) {
		String notRunLabel = StringConstants.NOT_RUN_LABEL;
		String notRunLabelSearchString = notRunLabel + ":";
		String notRunLabelPrefix = notRunLabel + "_";
		Matcher m = Pattern.compile(notRunLabelSearchString).matcher(scriptText);
		StringBuffer sb = new StringBuffer();
		int generatedIndex = 0;
		while (m.find()) {
			m.appendReplacement(sb, notRunLabelPrefix + (generatedIndex++) + ":");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public Object runScriptMethod(final String className, final String methodName, Object args, Binding binding)
			throws ResourceException, ScriptException, ClassNotFoundException {
		return getScript(getGroovyClassLoader().loadClass(className), binding, true).invokeMethod(methodName, args);
	}

	public Object runScriptMethod(final String className, final String methodName, Binding binding)
			throws ResourceException, ScriptException, ClassNotFoundException {
		return runScriptMethod(className, methodName, null, binding);
	}

	public Object runScript(final File file, Binding binding)
			throws ResourceException, ScriptException, IOException, ClassNotFoundException {
		return run(getGroovyCodeSource(file), binding, true);
	}

	public Script parseClass(final File file, Binding binding) throws IOException, ClassNotFoundException {
		return getScript(getGroovyCodeSource(file), binding, true);
	}

	private GroovyCodeSource getGroovyCodeSource(final File file) {
		try {
			return getGroovyCodeSource(FileUtils.readFileToString(file), FilenameUtils.getBaseName(file.getName()));
		} catch (IOException e) {
			return null;
		}
	}

	private GroovyCodeSource getGroovyCodeSource(final String scriptText, final String fileName) {
		GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>() {
			public GroovyCodeSource run() {
				return new GroovyCodeSource(scriptText, fileName, GroovyShell.DEFAULT_CODE_BASE);
			}
		});
		return gcs;
	}

	private Object run(GroovyCodeSource gcs, Binding binding, boolean remember)
			throws IOException, ClassNotFoundException {
		return getScript(gcs, binding, remember).run();
	}

	private Script getScript(GroovyCodeSource gcs, Binding binding, boolean remember)
			throws IOException, ClassNotFoundException {
		GroovyClassLoader classLoader = null;
		try {
			classLoader = new GroovyClassLoader(getParentClassLoader(),
					TestCaseExecutor.getConfigForExecutingScript(getGroovyClassLoader()));
			Class<?> clazz = classLoader.parseClass(gcs, remember);
			return getScript(clazz, binding, remember);
		} finally {
			if (classLoader != null) {
				classLoader.close();
			}
		}
	}

	public Script getScript(Class<?> clazz, Binding binding, boolean remember) {
		if (!remember) {
			return InvokerHelper.createScript(clazz, binding);
		}

		Script script = scriptLookup.get(clazz.getName());
		return script != null ? script : InvokerHelper.createScript(clazz, binding);
	}
}
