package com.kms.katalon.core.main;

import java.io.File;
import java.io.IOException;
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

import com.kms.katalon.core.annotation.SetUp;
import com.kms.katalon.core.annotation.TearDown;
import com.kms.katalon.core.annotation.TearDownIfError;
import com.kms.katalon.core.annotation.TearDownIfFailed;
import com.kms.katalon.core.annotation.TearDownIfPassed;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.driver.internal.DriverCleanerCollector;
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
import com.kms.katalon.core.util.internal.ExceptionsUtil;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class TestCaseExecutor {

    private static KeywordLogger logger = KeywordLogger.getInstance();

    private static ErrorCollector errorCollector = ErrorCollector.getCollector();

    protected TestResult testCaseResult;

    private TestCase testCase;

    private Stack<KeywordStackElement> keywordStack;

    private TestCaseMethodNodeCollector methodNodeCollector;

    private List<Throwable> parentErrors;

    protected ScriptEngine engine;

    protected Binding variableBinding;

    private TestCaseBinding testCaseBinding;

    private boolean doCleanUp;

    public TestCaseExecutor(String testCaseId, TestCaseBinding testCaseBinding, ScriptEngine engine,
            boolean doCleanUp) {
        this.testCaseBinding = testCaseBinding;
        this.engine = engine;
        this.testCase = TestCaseFactory.findTestCase(testCaseId);
        this.doCleanUp = doCleanUp;
    }

    public TestCaseExecutor(String testCaseId, TestCaseBinding testCaseBinding, ScriptEngine engine) {
        this(testCaseId, testCaseBinding, engine, false);
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

    protected File getScriptFile() throws IOException {
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

        logger.startTest(testCase.getTestCaseId(), getTestCaseProperties(testCaseBinding, testCase, flowControl),
                keywordStack);

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
            engine.changeConfigForExecutingScript();
            setupContextClassLoader();
            doExecute();
        } catch (ExceptionInInitializerError e) {
            // errors happened in static initializer like for Global Variable
            errorCollector.addError(e.getCause());
        } catch (Throwable e) {
            // logError(e, ExceptionsUtil.getMessageForThrowable(e));
            errorCollector.addError(e);
        }

        if (doCleanUp) {
            cleanUp();
        }

        if (errorCollector.containsErrors()) {
            onExecutionError(errorCollector.getFirstError());
        } else {
            onExecutionComplete();
        }
    }

    protected void doExecute() throws ResourceException, ScriptException, IOException, ClassNotFoundException {
        testCaseResult.setScriptResult(runScript(getScriptFile()));
    }

    private void cleanUp() {
        DriverCleanerCollector.getInstance().cleanDriversAfterRunningTestCase();
    }

    private Object runScript(File scriptFile)
            throws ResourceException, ScriptException, IOException, ClassNotFoundException {
        return engine.runScriptAsRawText(FileUtils.readFileToString(scriptFile),
                scriptFile.toURI().toURL().toExternalForm(), variableBinding);
    }

    protected void runMethod(File scriptFile, String methodName)
            throws ResourceException, ScriptException, ClassNotFoundException, IOException {
        engine.changeConfigForExecutingScript();
        engine.runScriptMethodAsRawText(FileUtils.readFileToString(scriptFile),
                scriptFile.toURI().toURL().toExternalForm(), methodName, variableBinding);
    }

    private Map<String, String> getTestCaseProperties(TestCaseBinding testCaseBinding, TestCase testCase,
            FailureHandling flowControl) {
        Map<String, String> testProperties = new HashMap<String, String>();
        testProperties.put(StringConstants.XML_LOG_NAME_PROPERTY, testCaseBinding.getTestCaseId());
        testProperties.put(StringConstants.XML_LOG_DESCRIPTION_PROPERTY, testCase.getDescription());
        testProperties.put(StringConstants.XML_LOG_ID_PROPERTY, testCase.getTestCaseId());
        testProperties.put(StringConstants.XML_LOG_SOURCE_PROPERTY, testCase.getMetaFilePath());
        testProperties.put(StringConstants.XML_LOG_IS_OPTIONAL,
                String.valueOf(flowControl == FailureHandling.OPTIONAL));
        return testProperties;
    }

    private Binding collectTestCaseVariables() {
        Binding variableBinding = new Binding();
        engine.changeConfigForCollectingVariable();

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
                Object defaultValueObject = engine.runScriptWithoutLogging(defaultValue, null);
                logger.logInfo(
                        MessageFormat.format(StringConstants.MAIN_LOG_INFO_VARIABLE_NAME_X_IS_SET_TO_Y_AS_DEFAULT,
                                testCaseVariable.getName(), defaultValueObject));
                variableBinding.setVariable(testCaseVariable.getName(), defaultValueObject);
            } catch (ExceptionInInitializerError e) {
                logger.logWarning(MessageFormat.format(StringConstants.MAIN_LOG_MSG_SET_TEST_VARIABLE_ERROR_BECAUSE_OF,
                        testCaseVariable.getName(), e.getCause().getMessage()));
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
        if (ignoreIfFailed) {
            startKeywordAttributeMap.put(StringConstants.XML_LOG_IS_IGNORED_IF_FAILED, String.valueOf(ignoreIfFailed));
        }
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
        TestCaseMethodNodeWrapper failedMethodWrapper = methodNodeCollector
                .getMethodNodeWrapper(TearDownIfFailed.class);
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
