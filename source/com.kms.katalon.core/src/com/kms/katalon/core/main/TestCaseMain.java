package com.kms.katalon.core.main;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.kms.katalon.core.annotation.AfterTestSuite;
import com.kms.katalon.core.annotation.BeforeTestSuite;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.context.internal.InternalTestSuiteContext;
import com.kms.katalon.core.context.internal.TestContextEvaluator;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCaseBinding;

import groovy.lang.GroovyClassLoader;

public class TestCaseMain {

    private static final int DELAY_TIME = 50;

    private static ScriptEngine engine;

    private static TestContextEvaluator contextEvaluator;

    /**
     * Setup test case or test suite before executing.
     *
     * CustomKeywords now has many custom keyword static methods, each one is
     * named with format [packageName].[className].[keywordName] but Groovy compiler
     * itself cannot invoke that formatted name. Therefore, we must change the
     * meta class of CustomKeywords to another one.
     * 
     * @throws IOException
     */
    public static void beforeStart() throws IOException {
        GroovyClassLoader classLoader = new GroovyClassLoader(TestCaseMain.class.getClassLoader());
        engine = ScriptEngine.getDefault(classLoader);

        // Load GlobalVariable class
        loadGlobalVariableClass(classLoader);
        loadCustomKeywordsClass(classLoader);

        String testListenerFolder = new File(RunConfiguration.getProjectDir(), "Test Listeners").getAbsolutePath();
        contextEvaluator = new TestContextEvaluator(testListenerFolder);
    }

    private static void loadCustomKeywordsClass(GroovyClassLoader cl) {
        // Load CustomKeywords class
        Class<?> clazz = cl.parseClass("class CustomKeywords { }");

        InvokerHelper.metaRegistry.setMetaClass(clazz, new CustomKeywordDelegatingMetaClass(clazz, cl));
    }

    private static void loadGlobalVariableClass(GroovyClassLoader cl) {
        try {
            cl.loadClass(StringConstants.GLOBAL_VARIABLE_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            try {
                cl.parseClass(new File(RunConfiguration.getProjectDir(), StringConstants.GLOBAL_VARIABLE_FILE_NAME));
            } catch (CompilationFailedException | IOException ignored) {

            }
        }
    }

    public static TestResult runTestCase(String testCaseId, TestCaseBinding testCaseBinding,
            FailureHandling flowControl) throws InterruptedException {
        Thread.sleep(DELAY_TIME);
        return new TestCaseExecutor(testCaseId, testCaseBinding, engine, contextEvaluator).execute(flowControl);
    }

    public static TestResult runTestCase(String testCaseId, TestCaseBinding testCaseBinding,
            FailureHandling flowControl, boolean doCleanUp) throws InterruptedException {
        Thread.sleep(DELAY_TIME);
        return new TestCaseExecutor(testCaseId, testCaseBinding, engine, contextEvaluator, doCleanUp)
                .execute(flowControl);
    }

    public static TestResult runTestCaseRawScript(String testScript, String testCaseId, TestCaseBinding testCaseBinding,
            FailureHandling flowControl) throws InterruptedException {
        Thread.sleep(DELAY_TIME);
        return new RawTestScriptExecutor(testScript, testCaseId, testCaseBinding, engine, contextEvaluator)
                .execute(flowControl);
    }

    public static TestResult runTestCaseRawScript(String testScript, String testCaseId, TestCaseBinding testCaseBinding,
            FailureHandling flowControl, boolean doCleanUp) throws InterruptedException {
        Thread.sleep(DELAY_TIME);
        return new RawTestScriptExecutor(testScript, testCaseId, testCaseBinding, engine, contextEvaluator, doCleanUp)
                .execute(flowControl);
    }

    public static void invokeStartSuite(String testSuiteId) {
        InternalTestSuiteContext testSuiteContext = new InternalTestSuiteContext();
        testSuiteContext.setTestSuiteId(testSuiteId);
        contextEvaluator.invokeListenerMethod(BeforeTestSuite.class.getName(), new Object[] { testSuiteContext });
    }

    public static void invokeEndSuite(String testSuiteId) {
        InternalTestSuiteContext testSuiteContext = new InternalTestSuiteContext();
        testSuiteContext.setTestSuiteId(testSuiteId);

        contextEvaluator.invokeListenerMethod(AfterTestSuite.class.getName(), new Object[] { testSuiteContext });
    }

    public static ScriptEngine getScriptEngine() {
        return engine;
    }
}
