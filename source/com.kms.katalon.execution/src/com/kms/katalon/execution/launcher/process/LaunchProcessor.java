package com.kms.katalon.execution.launcher.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.launching.JavaRuntime;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.ILaunchProcessor;

public class LaunchProcessor implements ILaunchProcessor {

    private static final String[] CANDIDATES_JAVA_FILES = { "java", "java.exe" };

    private static final String[] CANDIDATE_JAVA_LOCATIONS = { "bin" + File.separatorChar,
            "jre" + File.separatorChar + "bin" + File.separatorChar };

    private static final String STARTER_CLASS = "org.codehaus.groovy.tools.GroovyStarter";

    private static final String MAIN_CLASS = "groovy.ui.GroovyMain";

    private static final String GROOVY_BUNDLE_NAME = "org.codehaus.groovy";

    private String[] fClasspaths;

    private Map<String, String> environmentVariables;

    private String[] vmArgs;

    public LaunchProcessor(String[] classPaths) {
        this(classPaths, new HashMap<String, String>(), new String[0]);
    }

    public LaunchProcessor(String[] classPaths, Map<String, String> environmentVariables, String[] vmArgs) {
        this.fClasspaths = classPaths;
        this.environmentVariables = environmentVariables;
        this.vmArgs = vmArgs;
    }

    @Override
    public Process execute(File scripFile) throws IOException {
        List<String> args = new ArrayList<>();
        String[] commands = new String[] { getInstalledJRE(), "-cp",
                File.pathSeparator + FilenameUtils.separatorsToSystem(getGroovyLibs()) + File.pathSeparator
                        + getClasspaths(),
                STARTER_CLASS, "--main", MAIN_CLASS, FilenameUtils.separatorsToSystem(scripFile.getAbsolutePath()) };
        
        args.addAll(Arrays.asList(commands));
        if (vmArgs != null) {
            args.addAll(Arrays.asList(vmArgs));
        }
        ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[0]));
        pb.environment().putAll(getEnviromentVariables());
        pb.directory(new File(ProjectController.getInstance().getCurrentProject().getFolderLocation()));
        return pb.start();
    }

    private String getInstalledJRE() {
        File vmInstallLocation = JavaRuntime.getDefaultVMInstall().getInstallLocation();
        for (int i = 0; i < CANDIDATES_JAVA_FILES.length; i++) {
            for (int j = 0; j < CANDIDATE_JAVA_LOCATIONS.length; j++) {
                File javaFile = new File(vmInstallLocation, CANDIDATE_JAVA_LOCATIONS[j] + CANDIDATES_JAVA_FILES[i]);
                if (javaFile.isFile()) {
                    return javaFile.getAbsolutePath();
                }
            }
        }
        return "java";
    }

    @Override
    public String[] getClasspath() {
        return fClasspaths;
    }

    @Override
    public Map<String, String> getEnviromentVariables() throws IOException {
        return environmentVariables;
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
