package com.kms.katalon.execution.launcher.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.execution.launcher.ILaunchProcessor;

public class LaunchProcessor implements ILaunchProcessor {

    private static final String STARTER_CLASS = "org.codehaus.groovy.tools.GroovyStarter";

    private static final String MAIN_CLASS = "groovy.ui.GroovyMain";
    
    private static final String GROOVY_BUNDLE_NAME = "org.codehaus.groovy";

    private String[] fClasspaths;

    public LaunchProcessor(String[] classPaths) {
        fClasspaths = classPaths;
    }

    @Override
    public Process execute(File scripFile) throws IOException {

        ProcessBuilder pb = new ProcessBuilder("java", "-cp",
                FilenameUtils.separatorsToSystem(getGroovyLibs()) + File.pathSeparator + getClasspaths(), STARTER_CLASS,
                "--main", MAIN_CLASS, FilenameUtils.separatorsToSystem(scripFile.getAbsolutePath()));
        return pb.start();
    }

    @Override
    public String[] getClasspath() {
        return fClasspaths;
    }

    @Override
    public Map<String, String> getEnviromentVariables() throws IOException {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("groovy_home",
                getGroovyHome());

        return variables;
    }

    private String getGroovyHome() throws IOException {
        return FileLocator.getBundleFile(Platform.getBundle(GROOVY_BUNDLE_NAME)).getAbsolutePath();
    }

    private String getGroovyLibs() throws IOException {
        return getGroovyHome() + "/lib/*";
    }

    private String getClasspaths() {
        StringBuilder cpBuilder = new StringBuilder();
        for (String cp : getClasspath()) {
            cpBuilder.append(FilenameUtils.separatorsToSystem(cp)).append(File.pathSeparator);
        }

        return cpBuilder.toString();
    }

}
