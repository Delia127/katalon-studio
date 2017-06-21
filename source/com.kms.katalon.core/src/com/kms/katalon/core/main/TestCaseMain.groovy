package com.kms.katalon.core.main;

import org.codehaus.groovy.runtime.InvokerHelper

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCaseBinding

import groovy.transform.CompileStatic

@CompileStatic
public class TestCaseMain {

    private static final int DELAY_TIME = 50;
    private static ScriptEngine engine;

    /**
     * Setup test case or test suite before executing.
     *
     * CustomKeywords now has many custom keyword static methods, each one is
     * named with format [packageName].[className].[keywordName] but Groovy compiler
     * itself cannot invoke that formatted name. Therefore, we must change the
     * meta class of CustomKeywords to another one.
     */
    @CompileStatic
    public static void beforeStart() {
        GroovyClassLoader classLoader = new GroovyClassLoader(TestCaseMain.class.getClassLoader())
        engine = ScriptEngine.getDefault(classLoader)

        //Load GlobalVariable class
        loadGlobalVariableClass(classLoader)

        loadCustomKeywordsClass(classLoader)
    }

    private static loadCustomKeywordsClass(GroovyClassLoader cl) {
        try {
            //Load CustomKeywords class
            Class<?> clazz = cl.parseClass('''class CustomKeywords { }''')

            InvokerHelper.metaRegistry.setMetaClass(clazz, new CustomKeywordDelegatingMetaClass(clazz, cl));
        } catch (ClassNotFoundException e) {
            // Do nothing
        }
    }

    private static loadGlobalVariableClass(GroovyClassLoader cl) {
        try {
            cl.loadClass(StringConstants.GLOBAL_VARIABLE_CLASS_NAME)
        } catch (ClassNotFoundException ex) {
            cl.parseClass(new File(RunConfiguration.getProjectDir(), StringConstants.GLOBAL_VARIABLE_FILE_NAME))
        }
    }

    @CompileStatic
    public static TestResult runTestCase(String testCaseId, TestCaseBinding testCaseBinding, FailureHandling flowControl) {
        Thread.sleep(DELAY_TIME);
        return new TestCaseExecutor(testCaseId, testCaseBinding, engine).execute(flowControl)
    }
    
    @CompileStatic
    public static TestResult runTestCaseRawScript(String testScript, String testCaseId, TestCaseBinding testCaseBinding, FailureHandling flowControl) {
        Thread.sleep(DELAY_TIME);
        return new RawTestScriptExecutor(testScript, testCaseId, testCaseBinding, engine).execute(flowControl)
    }
}
