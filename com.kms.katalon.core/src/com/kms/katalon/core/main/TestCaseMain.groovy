package com.kms.katalon.core.main;

import groovy.transform.CompileStatic

import java.security.AccessController
import java.text.MessageFormat
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ImportNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.runtime.InvokerHelper

import com.kms.katalon.core.annotation.RequireAstTestStepTransformation
import com.kms.katalon.core.annotation.SetUp
import com.kms.katalon.core.annotation.TearDown
import com.kms.katalon.core.annotation.TearDownIfError
import com.kms.katalon.core.annotation.TearDownIfFailed
import com.kms.katalon.core.annotation.TearDownIfPassed
import com.kms.katalon.core.constants.StringConstants
import com.kms.katalon.core.exception.ExceptionsUtil
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.groovy.GroovyParser
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

@CompileStatic
public class TestCaseMain {

	private static final int DELAY_TIME = 500;
	private static KeywordLogger logger = KeywordLogger.getInstance();

	/**
	 * Setup test case or test suite before executing.
	 * 
	 * CustomKeywords now has many custom keyword static methods, each one is
	 * named with format [packageName].[className].[keywordName] but Groovy
	 * itself cannot invoke that formatted name. Therefore, we must change the
	 * meta class of CustomKeywords to another one.
	 */
	@CompileStatic
	public static void beforeStart() {
		try {
			Class<?> clazz = Class.forName(StringConstants.CUSTOM_KEYWORD_CLASS_NAME);
			InvokerHelper.metaRegistry.setMetaClass(clazz, new CustomKeywordDelegatingMetaClass(clazz));
		} catch (ClassNotFoundException e) {
			// Do nothing
		}
	}

	@CompileStatic
	public static TestResult runTestCase(String testCaseId, TestCaseBinding testCaseBinding, FailureHandling flowControl) {
		TestResult testResult = null;
		try {
			TestCase testCase = TestCaseFactory.findTestCase(testCaseId);
			String source = testCase.getMetaFilePath();
			Map<String, String> testProperties = new HashMap<String, String>();
			testProperties = new HashMap<String, String>();
			testProperties.put("name", testCaseBinding.getTestCaseId());
			testProperties.put("description", testCase.getDescription());
			testProperties.put("id", testCaseId);
			testProperties.put("source", source);
			testResult = internallyRunTestCase(testCaseId, testCaseBinding, testCase.getVariables(), testCase.getGroovyScriptPath(), testProperties, flowControl);
			Thread.sleep(DELAY_TIME);
		} catch (Exception e) {
			logger.logMessage(LogLevel.ERROR, ExceptionsUtil.getMessageForThrowable(e), e);
			if (testResult == null) {
				testResult = new TestResult();
				TestStatus status = new TestStatus();
				status.setStatusValue(TestStatusValue.ERROR);
				testResult.setMessage(e.getMessage());
				testResult.setTestStatus(status);
			}
		}
		return testResult;
	}

	@CompileStatic
	private static List<MethodNode> collectMethodWithAnnotation(List<MethodNode> methodList, Class<?> annotationClass) {
		List<MethodNode> annotatedMethods = new ArrayList<MethodNode>();
		if (methodList != null) {
			for (MethodNode method : methodList) {
				for (AnnotationNode annotationNode : method.getAnnotations()) {
					if (annotationNode.getClassNode().getName().equals(annotationClass.getName())
					|| annotationNode.getClassNode().getName().equals(annotationClass.getSimpleName())) {
						annotatedMethods.add(method);
					}
				}
			}
		}
		return annotatedMethods;
	}

	@CompileStatic
	private static void internallyRunMethods(GroovyShell shell, List<MethodNode> methodList, String importString,
			String startMessage) {
		if (methodList != null && methodList.size() > 0) {
			logger.logInfo(startMessage);
			for (MethodNode method : methodList) {
                Stack<KeywordStackElement> keywordStack = new Stack<KeywordStackElement>();
                logger.startKeyword(method.getName(), null, keywordStack);
				try {
					StringBuilder stringBuilder = new StringBuilder(importString);
					GroovyParser groovyParser = new GroovyParser(stringBuilder);
					groovyParser.parse(method.getCode());
					shell.evaluate(stringBuilder.toString());
					logger.logPassed(MessageFormat.format(StringConstants.MAIN_LOG_PASSED_METHOD_COMPLETED, method.getName()));
				} catch (Exception e) {
					logger.logWarning(MessageFormat.format(StringConstants.MAIN_LOG_WARNING_ERROR_OCCURRED_WHEN_RUN_METHOD,
							method.getName(), e.getClass().getName().toString(), ExceptionsUtil.getMessageForThrowable(e)));
				} finally {
                    endAllUnfinishedKeywords(keywordStack);
                    logger.endKeyword(method.getName(), null, keywordStack);
				}
			}
		}
	}

	@CompileStatic
	private static ClassNode getMainClassNode(List<ASTNode> astNodes) {
		for (ASTNode astNode : astNodes) {
			if (astNode instanceof ClassNode) {
				return ((ClassNode) astNode);
			}
		}
		return null;
	}

	@CompileStatic
	private static String getImportString(ClassNode classNode) {
		StringBuilder importString = new StringBuilder();
		GroovyParser groovyParser = new GroovyParser(importString);
		if (classNode != null) {
			ModuleNode moduleNode = classNode.getModule();
			if (moduleNode != null) {
				for (ImportNode importNode : moduleNode.getImports()) {
					groovyParser.parse(importNode);
				}
			}
		}
		return importString.toString();
	}

	@CompileStatic
	private static TestResult internallyRunTestCase(String testCaseId, TestCaseBinding testCaseBinding, List<Variable> testCaseVariables,
			String testCaseScriptFilePath, Map<String, String> testProperties, FailureHandling flowControl) throws Exception {
			
		TestResult testResult = new TestResult();
		TestStatus statusEntity = new TestStatus();
		statusEntity.setStatusValue(TestStatusValue.PASSED);
		testResult.setTestStatus(statusEntity);

		List<MethodNode> beforeRunMethods = null;
		List<MethodNode> afterRunMethods = null;
		List<MethodNode> afterRunPassedMethods = null;
		List<MethodNode> afterRunFailedMethods = null;
		List<MethodNode> afterRunErrorMethods = null;
		String importString = "";
		List<Throwable> parentErrors = ErrorCollector.getCollector().getCoppiedErrors();
		Stack<KeywordStackElement> keywordStack = new Stack<KeywordStackElement>();
		GroovyShell groovyShell = null;
		logger.startTest(testCaseId, testProperties, keywordStack, flowControl == FailureHandling.OPTIONAL);
		try {
			Binding binding = new Binding();

			if (testCaseBinding.getBindedValues() != null) {
				for (Entry<String, Object> entry : testCaseBinding.getBindedValues().entrySet()) {
					if (!(entry.getValue() instanceof TestDataColumn)) {
						binding.setVariable(entry.getKey(), entry.getValue());
					}
				}
			}

			groovyShell = new GroovyShell();
			for (Variable testCaseVariable : testCaseVariables) {
				if (!binding.hasVariable(testCaseVariable.getName())) {
					String defaultValue = testCaseVariable.getDefaultValue();
					if (defaultValue.isEmpty())
						defaultValue = "null";
					try {
						binding.setVariable(testCaseVariable.getName(), groovyShell.evaluate(defaultValue));
					} catch (GroovyRuntimeException e) {
						logger.logWarning(MessageFormat.format(StringConstants.MAIN_LOG_MSG_SET_TEST_VARIABLE_ERROR_BECAUSE_OF, testCaseVariable.getName(), e.getMessage()));
					}
				}
			}

			GroovyShell shell = getGroovyShell(new GroovyClassLoader(TestCaseMain.class.getClassLoader()), binding);
			List<ASTNode> astNodes = new AstBuilder().buildFromString(CompilePhase.CONVERSION, false,
					FileUtils.readFileToString(new File(testCaseScriptFilePath)));
			ClassNode classNode = getMainClassNode(astNodes);
			importString = getImportString(classNode);
			beforeRunMethods = collectMethodWithAnnotation(classNode.getMethods(), SetUp.class);
			afterRunMethods = collectMethodWithAnnotation(classNode.getMethods(), TearDown.class);
			afterRunPassedMethods = collectMethodWithAnnotation(classNode.getMethods(), TearDownIfPassed.class);
			afterRunFailedMethods = collectMethodWithAnnotation(classNode.getMethods(), TearDownIfFailed.class);
			afterRunErrorMethods = collectMethodWithAnnotation(classNode.getMethods(), TearDownIfError.class);

			ErrorCollector.getCollector().clearErrors();
			internallyRunMethods(shell, beforeRunMethods, importString, StringConstants.MAIN_MSG_START_RUNNING_SETUP_METHODS_FOR_TC);
			shell.evaluate(new File(testCaseScriptFilePath));
			if (ErrorCollector.getCollector().containsErrors()) {
				Throwable firstError = ErrorCollector.getCollector().getFirstError();
				if (!(firstError instanceof StepFailedException)) {
					logError(firstError, ExceptionsUtil.getMessageForThrowable(firstError));
				}
				endAllUnfinishedKeywords(keywordStack);
				logError(firstError,
					MessageFormat.format(StringConstants.MAIN_LOG_MSG_FAILED_BECAUSE_OF, testCaseId, ExceptionsUtil.getMessageForThrowable(firstError)));
				runTearDownMethodByError(firstError, shell, importString, afterRunFailedMethods, afterRunErrorMethods,
						afterRunMethods);
				statusEntity.setStatusValue(getResultByError(firstError, testCaseId));
			} else {
				endAllUnfinishedKeywords(keywordStack);
				internallyRunMethods(shell, afterRunPassedMethods, importString,
						StringConstants.MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_PASSED_TC);
				internallyRunMethods(shell, afterRunMethods, importString,
						StringConstants.MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_TC);
				logger.logPassed(testCaseId);
			}
		} catch (Throwable t) {
			if (!(t instanceof StepFailedException)) {
				logError(t, ExceptionsUtil.getMessageForThrowable(t));
			}
			endAllUnfinishedKeywords(keywordStack);
			statusEntity.setStatusValue(getResultByError(t, testCaseId));
			String message = MessageFormat.format(StringConstants.MAIN_LOG_MSG_FAILED_BECAUSE_OF, testCaseId, ExceptionsUtil.getMessageForThrowable(t));
			testResult.setMessage(message);
			logError(t, message);
			runTearDownMethodByError(t, groovyShell, importString, afterRunFailedMethods, afterRunErrorMethods,
					afterRunMethods);
		} finally {
			ErrorCollector.getCollector().getErrors().addAll(0, parentErrors);
			logger.endTest(testCaseId, null);
		}
		return testResult;
	}

	@CompileStatic
	private static void endAllUnfinishedKeywords(Stack<KeywordStackElement> keywordStack) {
		while (!keywordStack.isEmpty()) {
			KeywordStackElement keywordStackElement = keywordStack.pop();
			KeywordLogger.getInstance().endKeyword(keywordStackElement.getKeywordName(), null, keywordStackElement.getNestedLevel());
		}
	}

	@CompileStatic
	private static void runTearDownMethodByError(Throwable t, GroovyShell shell, String importString,
			List<MethodNode> afterRunFailedMethods, List<MethodNode> afterRunErrorMethods,
			List<MethodNode> afterRunMethods) {
		if (t.getClass().getName().equals(StepFailedException.class.getName()) || t instanceof AssertionError) {
			internallyRunMethods(shell, afterRunFailedMethods, importString,
					StringConstants.MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_FAILED_TC);
		} else {
			internallyRunMethods(shell, afterRunErrorMethods, importString,
					StringConstants.MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_ERROR_TC);
		}
		internallyRunMethods(shell, afterRunMethods, importString, StringConstants.MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_TC);
	}

	@CompileStatic
	private static TestStatusValue getResultByError(Throwable t, String testCaseId) {
		if (t.getClass().getName().equals(StepFailedException.class.getName()) || t instanceof AssertionError) {
			return TestStatusValue.FAILED;
		} else {
			return TestStatusValue.ERROR;
		}
	}
	
	@CompileStatic
	private static void logError(Throwable t, String message) {
		if (t.getClass().getName().equals(StepFailedException.class.getName()) || t instanceof AssertionError) {
			logger.logMessage(LogLevel.FAILED, message, t);
		} else {
			logger.logMessage(LogLevel.ERROR, message, t);
		}
	}

	@CompileStatic
	private static GroovyShell getGroovyShell(GroovyClassLoader loader, Binding binding) {
		CompilerConfiguration conf = new CompilerConfiguration(System.getProperties());
		conf.addCompilationCustomizers(new ASTTransformationCustomizer(RequireAstTestStepTransformation.class));
		GroovyShell groovyShell = null;
		if (binding != null) {
			groovyShell = new GroovyShell(loader, binding, conf);
		} else {
			groovyShell = new GroovyShell(loader, new Binding(), conf);
		}
		setupContextClassLoader(groovyShell);
		return groovyShell;
	}

	@CompileStatic
	public static void setupContextClassLoader(GroovyShell shell) {
		AccessController.doPrivileged(new DoSetContextAction(Thread.currentThread(), shell.getClassLoader()));
	}
}
