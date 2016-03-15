package com.kms.katalon.composer.execution.launcher;

import java.util.Arrays;
import java.util.Map;

import org.codehaus.groovy.eclipse.launchers.GroovyScriptLaunchShortcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

import com.kms.katalon.execution.launcher.model.LaunchMode;

public class IDELaunchShorcut extends GroovyScriptLaunchShortcut {

    public IDELaunchShorcut() {
        super();
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

    /**
     * @throws CoreException
     * @see #launchGroovy(ICompilationUnit, IJavaProject, String)
     */
    private ILaunch internallyLaunchGroovy(ICompilationUnit unit, String mode) throws CoreException {
        IType[] types = unit.getAllTypes();
        IType runType = findClassToRun(types);

        if (runType == null) {
            return null;
        }

        IJavaProject javaProject = unit.getJavaProject();
        Map<String, String> launchConfigProperties = createLaunchProperties(runType, javaProject);

        ILaunchConfigurationWorkingCopy workingConfig = findOrCreateLaunchConfig(launchConfigProperties,
                runType.getElementName());
        workingConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH,
                Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(javaProject)));
        ILaunchConfiguration config = workingConfig.doSave();

        return config.launch(mode, new NullProgressMonitor());
    }

    public ILaunch launch(IFile scriptFile, LaunchMode launchMode) throws CoreException {
        if (scriptFile == null) {
            return null;
        }

        ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(scriptFile);
        return (compilationUnit != null) ? internallyLaunchGroovy(compilationUnit, launchMode.toString()) : null;
    }

}
