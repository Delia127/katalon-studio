package com.kms.katalon.composer.report.platform;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class ExportReportProviderReflection {

    private final ExportReportProviderPlugin exportPluginProvider;

    private static Map<String, String> compilerProperties = new HashMap<>();
    public static final String CLASSPATH_PROPERTY = "java.class.path";

    public ExportReportProviderReflection(ExportReportProviderPlugin pluginProvider) {
        this.exportPluginProvider = pluginProvider;
    }

    public String[] getSupportedFormatTypeForTestSuite() throws MalformedURLException, CoreException {
        URLClassLoader projectClassLoader = ProjectController.getInstance()
                .getProjectClassLoader(ProjectController.getInstance().getCurrentProject());
        Binding binding = new Binding();
        binding.setVariable("exportProvider", exportPluginProvider.getProvider());
        GroovyShell groovyShell = new GroovyShell(projectClassLoader, binding);
        return (String[]) groovyShell.evaluate("exportProvider.getSupportedTypeForTestSuite()");
    }

    public String[] getSupportedFormatTypeForTestSuiteCollection() throws MalformedURLException, CoreException {
        URLClassLoader projectClassLoader = ProjectController.getInstance()
                .getProjectClassLoader(ProjectController.getInstance().getCurrentProject());
        Binding binding = new Binding();
        binding.setVariable("exportProvider", exportPluginProvider.getProvider());
        GroovyShell groovyShell = new GroovyShell(projectClassLoader, binding);
        return (String[]) groovyShell.evaluate("exportProvider.getSupportedTypeForTestSuiteCollection()");
    }

    public Object exportTestSuite(ReportEntity report, String formatType, File exportedLocation)
            throws MalformedURLException, CoreException, ReflectiveOperationException {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        URLClassLoader projectClassLoader = ProjectController.getInstance()
                .getProjectClassLoader(currentProject);

        Binding binding = new Binding();
        binding.setVariable("exportProvider", exportPluginProvider.getProvider());
        binding.setVariable("fileLocation", exportedLocation);
        binding.setVariable("projectDir", currentProject.getFolderLocation());
        binding.setVariable("reportId", report.getIdForDisplay());
        binding.setVariable("formatType", formatType);
        GroovyShell groovyShell = new GroovyShell(projectClassLoader, binding);
        return groovyShell.evaluate("exportProvider.exportTestSuite(fileLocation, projectDir, reportId, formatType)");
    }

    public Object exportTestSuiteCollection(ReportCollectionEntity report, String formatType, File exportedLocation)
            throws MalformedURLException, CoreException {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        URLClassLoader projectClassLoader = ProjectController.getInstance()
                .getProjectClassLoader(currentProject);

        Binding binding = new Binding();
        binding.setVariable("exportProvider", exportPluginProvider.getProvider());
        binding.setVariable("fileLocation", exportedLocation);
        binding.setVariable("projectDir", currentProject.getFolderLocation());
        binding.setVariable("reportId", report.getIdForDisplay());
        binding.setVariable("formatType", formatType);
        GroovyShell groovyShell = new GroovyShell(projectClassLoader, binding);
        return groovyShell.evaluate("exportProvider.exportTestSuiteCollection(fileLocation, projectDir, reportId, formatType)");
    }
}
