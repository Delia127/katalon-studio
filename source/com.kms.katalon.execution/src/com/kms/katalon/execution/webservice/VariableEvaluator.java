package com.kms.katalon.execution.webservice;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.main.TestCaseMain;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;

import groovy.lang.GroovyShell;

public class VariableEvaluator {

    public Map<String, Object> evaluate(String testObjectId, Map<String, String> variables) throws Exception {
        
        Map<String, Object> evaluatedVariables = new HashMap<>();
        
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addImport(TestDataFactory.class.getSimpleName(), TestDataFactory.class.getName());
        importCustomizer.addImport(ObjectRepository.class.getSimpleName(), ObjectRepository.class.getName());
        importCustomizer.addImport(TestCaseFactory.class.getSimpleName(), TestCaseFactory.class.getName());
        importCustomizer.addImport(RunConfiguration.class.getSimpleName(), RunConfiguration.class.getName());
        importCustomizer.addStaticImport(TestDataFactory.class.getName(), "findTestData");
        importCustomizer.addStaticImport(ObjectRepository.class.getName(), "findTestObject");
        importCustomizer.addStaticImport(TestCaseFactory.class.getName(), "findTestCase");
        importCustomizer.addImport(TestCaseMain.class.getSimpleName(), TestCaseMain.class.getName());
        importCustomizer.addImport("GlobalVariable", "internal.GlobalVariable");
        
        CompilerConfiguration compilerConfig = new CompilerConfiguration();
        compilerConfig.addCompilationCustomizers(importCustomizer);
        
        URLClassLoader classLoader = ProjectController.getInstance().getProjectClassLoader(
                ProjectController.getInstance().getCurrentProject());
//        URL[] urls = classLoader.getURLs();
//        for (URL url : urls) {
//            System.out.println(url.toString());
//        }
        GroovyShell groovyShell = new GroovyShell(classLoader, compilerConfig);
        for (Map.Entry<String, String> variableEntry : variables.entrySet()) {
            Object evaluatedValue = groovyShell.evaluate("RunConfiguration.getExecutionProfile()");
            evaluatedVariables.put(variableEntry.getKey(), evaluatedValue);
        }
        
        return evaluatedVariables;
    }
}
