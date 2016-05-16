package com.kms.katalon.core.main;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.kms.katalon.core.annotation.SetUp;
import com.kms.katalon.core.annotation.TearDown;
import com.kms.katalon.core.annotation.TearDownIfError;
import com.kms.katalon.core.annotation.TearDownIfFailed;
import com.kms.katalon.core.annotation.TearDownIfPassed;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.ErrorCollector;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.logging.KeywordLogger.KeywordStackElement;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testcase.TestCaseBinding;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testcase.Variable;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.util.ExceptionsUtil;

public class TestCaseExecutor {
    private static KeywordLogger logger = KeywordLogger.getInstance();

    private static ErrorCollector errorCollector = ErrorCollector.getCollector();

    private TestResult testCaseResult;

    private TestCase testCase;

    private Stack<KeywordStackElement> keywordStack;

    private TestCaseMethodNodeCollector methodNodeCollector;

    private List<Throwable> parentErrors;

    private ScriptEngine engine;

    private Binding variableBinding;

    private TestCaseBinding testCaseBinding;

    public TestCaseExecutor(String testCaseId, TestCaseBinding testCaseBinding, ScriptEngine engine) {
        this.testCaseBinding = testCaseBinding;
        this.engine = engine;
        this.testCase = TestCaseFactory.findTestCase(testCaseId);
    }

    private void preExecution() {
        testCaseResult = TestResult.getDefault();
        keywordStack = new Stack<KeywordLogger.KeywordStackElement>();
        parentErrors = errorCollector.getCoppiedErrors();
        errorCollector.clearErrors();
    }

    private void onExecutionComplete() {
        endAllUnfinishedKeywords(keywordStack);

        internallyRunMethods(methodNodeCollector.getMethodNodeWrapper(TearDownIfPassed.class));

        internallyRunMethods(methodNodeCollector.getMethodNodeWrapper(TearDown.class));
        logger.logPassed(testCase.getTestCaseId());
    }

    private void onExecutionError(Throwable t) {
        if (!keywordStack.isEmpty()) {
            endAllUnfinishedKeywords(keywordStack);
        }
        testCaseResult.getTestStatus().setStatusValue(getResultByError(t));
        String message = MessageFormat.format(StringConstants.MAIN_LOG_MSG_FAILED_BECAUSE_OF, testCase.getTestCaseId(),
                ExceptionsUtil.getMessageForThrowable(t));
        testCaseResult.setMessage(message);
        logError(t, message);

        runTearDownMethodByError(t);
    }

    private boolean processScriptPreparationPhase() {
        // Collect AST nodes for script of test case
        try {
            methodNodeCollector = new TestCaseMethodNodeCollector(testCase);
        } catch (IOException e) {
            onSetupError(e);
            return false;
        }

        try {
            variableBinding = collectTestCaseVariables();
        } catch (CompilationFailedException e) {
            onSetupError(e);
            return false;
        }
        return true;
    }

    private boolean processSetupPhase() {
        // Run setup method
        internallyRunMethods(methodNodeCollector.getMethodNodeWrapper(SetUp.class));
        boolean setupFailed = errorCollector.containsErrors();
        if (setupFailed) {
            internallyRunMethods(methodNodeCollector.getMethodNodeWrapper(TearDownIfError.class));
            internallyRunMethods(methodNodeCollector.getMethodNodeWrapper(TearDown.class));
            onSetupError(errorCollector.getFirstError());
        }
        return !setupFailed;
    }

    private File getScriptFile() throws IOException {
        return new File(testCase.getGroovyScriptPath());
    }

    private void onSetupError(Throwable t) {
        String message = MessageFormat.format(StringConstants.MAIN_LOG_MSG_ERROR_BECAUSE_OF, testCase.getTestCaseId(),
                ExceptionsUtil.getMessageForThrowable(t));
        testCaseResult.setMessage(message);
        testCaseResult.getTestStatus().setStatusValue(TestStatusValue.ERROR);
        logger.logError(message);
    }

    private void postExecution() {
        errorCollector.getErrors().addAll(0, parentErrors);
    }

    public TestResult execute(FailureHandling flowControl) {
        preExecution();

        logger.startTest(testCase.getTestCaseId(), getTestCaseProperties(testCaseBinding, testCase), keywordStack,
                flowControl == FailureHandling.OPTIONAL);

        accessMainPhase();

        logger.endTest(testCase.getTestCaseId(), null);

        postExecution();
        return testCaseResult;
    }

    private void accessMainPhase() {
        if (!processScriptPreparationPhase()) {
            return;
        }

        if (!processSetupPhase()) {
            return;
        }

        processExecutionPhase();
    }

    private void processExecutionPhase() {
        try {
            // Prepare configuration before execution
            engine.setConfig(getConfigForExecutingScript());
            setupContextClassLoader();
            testCaseResult.setScriptResult(runScript(getScriptFile()));
        } catch (Throwable e) {
            logError(e, ExceptionsUtil.getMessageForThrowable(e));
            errorCollector.addError(e);
        }

        if (errorCollector.containsErrors()) {
            onExecutionError(errorCollector.getFirstError());
        } else {
            onExecutionComplete();
        }
    }

    private Object runScript(File scriptFile) throws ResourceException, ScriptException, IOException {
        return engine.runScriptAsRawText(FileUtils.readFileToString(scriptFile), scriptFile.getName(), variableBinding);
    }

    private void runMethod(File scriptFile, String methodName) throws ResourceException, ScriptException,
            ClassNotFoundException, IOException {
        engine.runScriptMethodAsRawText(FileUtils.readFileToString(scriptFile), scriptFile.getName(), methodName,
                variableBinding);
    }

    private Map<String, String> getTestCaseProperties(TestCaseBinding testCaseBinding, TestCase testCase) {
        Map<String, String> testProperties = new HashMap<String, String>();
        testProperties.put("name", testCaseBinding.getTestCaseId());
        testProperties.put("description", testCase.getDescription());
        testProperties.put("id", testCase.getTestCaseId());
        testProperties.put("source", testCase.getMetaFilePath());
        return testProperties;
    }

    @SuppressWarnings("unchecked")
    private CompilerConfiguration getConfigForExecutingScript() throws ClassNotFoundException {
        CompilerConfiguration conf = new CompilerConfiguration(System.getProperties());
        Class<?> astTransformationClass = engine.getGroovyClassLoader().loadClass(
                StringConstants.TEST_STEP_TRANSFORMATION_CLASS);

        conf.addCompilationCustomizers(new ASTTransformationCustomizer(
                (Class<? extends Annotation>) astTransformationClass));
        return conf;
    }

    private CompilerConfiguration getConfigForCollectingVariable() {
        CompilerConfiguration configuration = new CompilerConfiguration();
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addImport(TestDataFactory.class.getSimpleName(), TestDataFactory.class.getName());
        importCustomizer.addImport(ObjectRepository.class.getSimpleName(), ObjectRepository.class.getName());

        configuration.addCompilationCustomizers(importCustomizer);
        return configuration;
    }

    private Binding collectTestCaseVariables() {
        Binding variableBinding = new Binding();
        engine.setConfig(getConfigForCollectingVariable());

        logger.logInfo(StringConstants.MAIN_LOG_INFO_START_EVALUATE_VARIABLE);

        if (testCaseBinding.getBindedValues() != null) {
            for (Entry<String, Object> entry : testCaseBinding.getBindedValues().entrySet()) {
                logger.logInfo(MessageFormat.format(StringConstants.MAIN_LOG_INFO_VARIABLE_NAME_X_IS_SET_TO_Y,
                        entry.getKey(), entry.getValue()));
                variableBinding.setVariable(entry.getKey(), entry.getValue());
            }
        }

        for (Variable testCaseVariable : testCase.getVariables()) {
            if (variableBinding.hasVariable(testCaseVariable.getName())) {
                continue;
            }

            try {
                String defaultValue = StringUtils.defaultIfEmpty(testCaseVariable.getDefaultValue(),
                        StringConstants.NULL_AS_STRING);
                Object defaultValueObject = engine.runScript(defaultValue, null);
                logger.logInfo(MessageFormat.format(
                        StringConstants.MAIN_LOG_INFO_VARIABLE_NAME_X_IS_SET_TO_Y_AS_DEFAULT,
                        testCaseVariable.getName(), defaultValueObject));
                variableBinding.setVariable(testCaseVariable.getName(), defaultValueObject);
            } catch (Exception e) {
                logger.logWarning(MessageFormat.format(StringConstants.MAIN_LOG_MSG_SET_TEST_VARIABLE_ERROR_BECAUSE_OF,
                        testCaseVariable.getName(), e.getMessage()));
            }
        }

        return variableBinding;
    }

    private void logError(Throwable t, String message) {
        logger.logMessage(ErrorCollector.fromError(t), message, t);
    }

    private TestStatusValue getResultByError(Throwable t) {
        return TestStatusValue.valueOf(ErrorCollector.fromError(t).name());
    }

    private void endAllUnfinishedKeywords(Stack<KeywordStackElement> keywordStack) {
        while (!keywordStack.isEmpty()) {
            KeywordStackElement keywordStackElement = keywordStack.pop();
            logger.endKeyword(keywordStackElement.getKeywordName(), null, keywordStackElement.getNestedLevel());
        }
    }

    private void internallyRunMethods(TestCaseMethodNodeWrapper methodNodeWrapper) {
        List<MethodNode> methodList = methodNodeWrapper.getMethodNodes();
        if (methodList == null || methodList.isEmpty()) {
            return;
        }

        logger.logInfo(methodNodeWrapper.getStartMessage());
        int count = 1;
        for (MethodNode method : methodList) {
            runMethod(method.getName(), count++, methodNodeWrapper.isIgnoredIfFailed());
        }
    }

    private void runMethod(String methodName, int index, boolean ignoreIfFailed) {
        Stack<KeywordStackElement> keywordStack = new Stack<KeywordStackElement>();
        Map<String, String> startKeywordAttributeMap = new HashMap<String, String>();
        startKeywordAttributeMap.put(StringConstants.XML_LOG_STEP_INDEX, String.valueOf(index));
        logger.startKeyword(methodName, startKeywordAttributeMap, keywordStack);
        try {
            runMethod(getScriptFile(), methodName);
            endAllUnfinishedKeywords(keywordStack);
            logger.logPassed(MessageFormat.format(StringConstants.MAIN_LOG_PASSED_METHOD_COMPLETED, methodName));
        } catch (Throwable e) {
            endAllUnfinishedKeywords(keywordStack);
            String message = MessageFormat.format(StringConstants.MAIN_LOG_WARNING_ERROR_OCCURRED_WHEN_RUN_METHOD,
                    methodName, e.getClass().getName(), ExceptionsUtil.getMessageForThrowable(e));
            if (ignoreIfFailed) {
                logger.logWarning(message);
                return;
            }
            logger.logError(message);
            errorCollector.addError(e);
        } finally {
            logger.endKeyword(methodName, null, keywordStack);
        }
    }

    private void runTearDownMethodByError(Throwable t) {
        LogLevel errorLevel = ErrorCollector.fromError(t);
        TestCaseMethodNodeWrapper failedMethodWrapper = methodNodeCollector.getMethodNodeWrapper(TearDownIfFailed.class);
        if (errorLevel == LogLevel.ERROR) {
            failedMethodWrapper = methodNodeCollector.getMethodNodeWrapper(TearDownIfError.class);
        }

        internallyRunMethods(failedMethodWrapper);
        internallyRunMethods(methodNodeCollector.getMethodNodeWrapper(TearDown.class));
    }

    @SuppressWarnings("unchecked")
    public void setupContextClassLoader() {
        AccessController.doPrivileged(new DoSetContextAction(Thread.currentThread(), engine.getGroovyClassLoader()));
    }
}
