package com.kms.katalon.core.main;

import groovy.lang.Binding;
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
        URL[] roots = new URL[] { new File(RunConfiguration.getProjectDir(), StringConstants.CUSTOM_KEYWORD_FOLDER_NAME).toURI()
                .toURL() };
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

    public Object runScript(final String scriptText, Binding binding) throws ResourceException, ScriptException {
        // Parse this temporary class without caching
        return run(getGroovyCodeSource(scriptText, generateScriptName()), binding, false);
    }

    public Object runScript(final String className, final String methodName, Object args, Binding binding)
            throws ResourceException, ScriptException, ClassNotFoundException {

        return getScript(getGroovyClassLoader().loadClass(className), binding, true).invokeMethod(methodName, args);
    }
    
    public Object runScript(final String className, final String methodName, Binding binding)
            throws ResourceException, ScriptException, ClassNotFoundException {

        return runScript(className, methodName, null, binding);
    }

    public Object runScript(final File file, Binding binding) throws ResourceException, ScriptException {
        return run(getGroovyCodeSource(file), binding, true);
    }

    public Script parseClass(final File file, Binding binding) {
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

    private Object run(GroovyCodeSource gcs, Binding binding, boolean remember) {
        return getScript(gcs, binding, remember).run();
    }

    private Script getScript(GroovyCodeSource gcs, Binding binding, boolean remember) {
        Class<?> clazz = getGroovyClassLoader().parseClass(gcs, remember);

        return getScript(clazz, binding, remember);
    }

    public Script getScript(Class<?> clazz, Binding binding, boolean remember) {

        if (!remember) {
            return InvokerHelper.createScript(clazz, binding);
        }

        Script script = scriptLookup.get(clazz.getName());
        return script != null ? script : InvokerHelper.createScript(clazz, binding);
    }
}
