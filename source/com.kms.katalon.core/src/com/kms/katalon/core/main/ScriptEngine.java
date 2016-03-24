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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;

public class ScriptEngine extends GroovyScriptEngine {
    // Used to generate new temp script name
    private int counter;

    public static ScriptEngine getDefault(ClassLoader parentClassLoader) throws IOException {
        URL[] roots = new URL[] { new File(RunConfiguration.getProjectDir(), StringConstants.CUSTOM_KEYWORD_FOLDER_NAME)
                .toURI().toURL() };
        return new ScriptEngine(roots, parentClassLoader);
    }

    public ScriptEngine(URL[] roots, ClassLoader parentClassLoader) {
        super(roots, parentClassLoader);
        counter = 0;
    }

    public Object runScript(final String scriptText, Binding binding) throws ResourceException, ScriptException {
        final String finalName = generateScriptName();
        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>() {
            public GroovyCodeSource run() {
                return new GroovyCodeSource(scriptText, finalName, GroovyShell.DEFAULT_CODE_BASE);
            }
        });

        // Parse this temporary class without caching
        return run(gcs, binding);
    }

    public Object runScript(final File file, Binding binding) throws ResourceException, ScriptException {

        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>() {
            public GroovyCodeSource run() {
                try {
                    return new GroovyCodeSource(FileUtils.readFileToString(file), FilenameUtils.getBaseName(file
                            .getName()), GroovyShell.DEFAULT_CODE_BASE);
                } catch (IOException e) {
                    return null;
                }
            }
        });

        return run(gcs, binding);
    }

    private Object run(GroovyCodeSource gcs, Binding binding) {
        // Parse this temporary class without caching
        Class<?> clazz = getGroovyClassLoader().parseClass(gcs, false);

        Script script = InvokerHelper.createScript(clazz, binding);

        return script.run();
    }

    protected synchronized String generateScriptName() {
        return "Script" + (++counter) + "." + StringConstants.SCRIPT_FILE_EXT;
    }

}
