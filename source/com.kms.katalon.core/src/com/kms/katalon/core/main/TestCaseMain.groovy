package com.kms.katalon.core.main;

import groovy.transform.CompileStatic

import java.security.AccessController
import java.text.MessageFormat
import java.util.Map.Entry

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ImportNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.InvokerHelper

import com.google.gson.InstanceCreator;
import com.kms.katalon.core.annotation.SetUp
import com.kms.katalon.core.annotation.TearDown
import com.kms.katalon.core.annotation.TearDownIfError
import com.kms.katalon.core.annotation.TearDownIfFailed
import com.kms.katalon.core.annotation.TearDownIfPassed
import com.kms.katalon.core.ast.AstTextValueUtil;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.core.ast.RequireAstTestStepTransformation;
import com.kms.katalon.core.constants.StringConstants
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.logging.ErrorCollector
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.logging.LogLevel
import com.kms.katalon.core.logging.KeywordLogger.KeywordStackElement
import com.kms.katalon.core.logging.model.TestStatus
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseBinding
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testcase.Variable
import com.kms.katalon.core.testdata.TestDataColumn
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.util.ExceptionsUtil;
import com.kms.katalon.core.configuration.RunConfiguration;

import org.codehaus.groovy.control.CompilationUnit;

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
}
