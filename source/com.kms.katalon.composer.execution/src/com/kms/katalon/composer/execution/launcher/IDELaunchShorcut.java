package com.kms.katalon.composer.execution.launcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.eclipse.launchers.GroovyScriptLaunchShortcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class IDELaunchShorcut extends GroovyScriptLaunchShortcut {

    private static final String JVM_ARGUMENT_MAX_MEMORY_KEY = "-Xmx";

    public IDELaunchShorcut() {
        super();
    }

    @Override
    public ILaunchConfigurationType getGroovyLaunchConfigType() {
        return getLaunchManager().getLaunchConfigurationType(StringConstants.LAUNCH_CONFIGURATION_TYPE_ID);
    }

    @Override
    protected String classToRun() {
        return "groovy.ui.GroovyMain";
    }

    public static void cleanConfiguration(ILaunch launch) {
        getLaunchManager().removeLaunch(launch);
    }

    public static void cleanAllConfigurations() throws CoreException {
        while (getLaunchManager().getLaunchConfigurations().length > 0) {
            getLaunchManager().getLaunchConfigurations()[0].delete();
        }
    }

    private ILaunch internallyLaunchGroovy(ICompilationUnit unit, String mode, IRunConfiguration runConfiguration)
            throws CoreException {
        IType[] types = unit.getAllTypes();
        IType runType = findClassToRun(types);

        if (runType == null) {
            return null;
        }

        IJavaProject javaProject = unit.getJavaProject();
        Map<String, String> launchConfigProperties = createLaunchProperties(runType, javaProject);

        ILaunchConfigurationWorkingCopy workingConfig = findOrCreateLaunchConfig(launchConfigProperties,
                runType.getElementName());
        Map<String, String> environmentVariables = new HashMap<>();
        try {
            environmentVariables = runConfiguration.getAdditionalEnvironmentVariables();
        } catch (IOException | ExecutionException ignored) {
        }
        if (environmentVariables != null && !environmentVariables.isEmpty()) {
            workingConfig.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, environmentVariables);
        }
        String vmArguments = workingConfig.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
        if (!vmArguments.contains(JVM_ARGUMENT_MAX_MEMORY_KEY)) {
            vmArguments += " " + JVM_ARGUMENT_MAX_MEMORY_KEY + byteToMegabytes(Runtime.getRuntime().maxMemory()) + "m";
            workingConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments);
        }
        List<String> vmArgs = Arrays.asList(runConfiguration.getVmArgs());
        for (String eachArg : vmArgs) {
            vmArguments += " " + eachArg;
        }
        workingConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH,
                Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(javaProject)));
        ILaunchConfiguration config = workingConfig.doSave();
        config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");

        return config.launch(mode, new NullProgressMonitor());
    }

    private static long byteToMegabytes(long maxMemory) {
        return maxMemory / (1024 * 1024);
    }
    
    public ILaunch launch(IFile scriptFile, LaunchMode launchMode, IRunConfiguration runConfiguration) throws CoreException {
        if (scriptFile == null) {
            return null;
        }

        ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(scriptFile);
        return (compilationUnit != null) ? internallyLaunchGroovy(compilationUnit, launchMode.toString(), runConfiguration) : null;
    }

}
