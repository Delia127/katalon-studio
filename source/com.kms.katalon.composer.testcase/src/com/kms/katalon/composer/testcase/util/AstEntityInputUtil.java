package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapEntryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.variable.VariableEntity;

/**
 * Utility class to help processing ast node for katalon entities
 */
public class AstEntityInputUtil {
    public static final String GET_VALUE_METHOD_NAME = "getValue";
    public static final String FIND_TEST_CASE_METHOD_NAME = "findTestCase";
    public static final String FIND_TEST_DATA_METHOD_NAME = "findTestData";
    public static final String FIND_TEST_OBJECT_METHOD_NAME = "findTestObject";
    public static final String CALL_TEST_CASE_METHOD_NAME = "callTestCase";

    public static MethodCallExpressionWrapper getNewCallTestCaseExpression(TestCaseEntity testCase,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = new MethodCallExpressionWrapper(TestCaseFactory.class,
                FIND_TEST_CASE_METHOD_NAME, parentNode);
        ArgumentListExpressionWrapper argument = (ArgumentListExpressionWrapper) objectMethodCall.getArguments();
        argument.addExpression(new ConstantExpressionWrapper(testCase != null ? testCase.getIdForDisplay() : null,
                argument));
        return objectMethodCall;
    }

    public static boolean isCallTestCaseMethod(MethodCallExpressionWrapper methodCallExpression) {
        return (methodCallExpression.getObjectExpressionAsString().contains(
                AstTreeTableInputUtil.BUILT_IN_KEYWORDS_CLASS_NAME)
                && methodCallExpression.getMethodAsString().equals(CALL_TEST_CASE_METHOD_NAME) && methodCallExpression
                    .getArguments() instanceof ArgumentListExpressionWrapper);
    }

    public static boolean isCallTestCaseArgument(MethodCallExpressionWrapper callTestCaseMethodCallExpression) {
        return (callTestCaseMethodCallExpression != null
                && callTestCaseMethodCallExpression.isObjectExpressionOfClass(TestCaseFactory.class)
                && callTestCaseMethodCallExpression.getMethodAsString().equals(FIND_TEST_CASE_METHOD_NAME)
                && callTestCaseMethodCallExpression.getArguments() instanceof ArgumentListExpressionWrapper && ((ArgumentListExpressionWrapper) callTestCaseMethodCallExpression
                    .getArguments()).getExpressions().size() == 1);
    }

    public static ExpressionWrapper getCallTestCaseParam(MethodCallExpressionWrapper methodCallExpression) {
        if (!isCallTestCaseArgument(methodCallExpression)) {
            return null;
        }
        return ((ArgumentListExpressionWrapper) methodCallExpression.getArguments()).getExpression(0);
    }

    public static boolean setCallTestCaseParam(MethodCallExpressionWrapper methodCallExpression, String testCaseValue) {
        if (!isCallTestCaseArgument(methodCallExpression)) {
            return false;
        }
        ((ConstantExpressionWrapper) ((ArgumentListExpressionWrapper) methodCallExpression.getArguments())
                .getExpression(0)).setValue(testCaseValue);
        return true;
    }

    public static ExpressionStatementWrapper generateCallTestCaseExpresionStatement(TestCaseEntity testCase,
            ASTNodeWrapper parentNode) {
        KeywordClass defaultBuiltinKeywordContributor = null;
        try {
            defaultBuiltinKeywordContributor = TestCasePreferenceDefaultValueInitializer.getDefaultKeywordType();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (defaultBuiltinKeywordContributor == null) {
            return null;
        }
        MethodCallExpressionWrapper keywordMethodCallExpressionWrapper = new MethodCallExpressionWrapper(
                defaultBuiltinKeywordContributor.getSimpleName(), BuiltInMethodNodeFactory.CALL_TEST_CASE_METHOD_NAME,
                null);
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) keywordMethodCallExpressionWrapper
                .getArguments();
        argumentList.addExpression(getNewCallTestCaseExpression(testCase, argumentList));
        argumentList.addExpression(generateTestCaseVariableBindingExpression(testCase, argumentList));
        argumentList.addExpression(AstTreeTableInputUtil.getNewFailureHandlingPropertyExpression(argumentList));
        return new ExpressionStatementWrapper(keywordMethodCallExpressionWrapper, parentNode);
    }

    public static MapExpressionWrapper generateTestCaseVariableBindingExpression(TestCaseEntity testCase,
            ASTNodeWrapper parent) {
        boolean generateDefaultValue = TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue();
        MapExpressionWrapper mapExpression = new MapExpressionWrapper(parent);
        List<MapEntryExpressionWrapper> variableExpressions = new ArrayList<MapEntryExpressionWrapper>();
        for (VariableEntity variableEntity : testCase.getVariables()) {
            ConstantExpressionWrapper keyExpression = new ConstantExpressionWrapper(variableEntity.getName(), null);
            String variableValue = variableEntity.getDefaultValue();
            if (!generateDefaultValue) {
                variableValue = variableEntity.getName();
            }
            ExpressionWrapper valueExpression = (variableValue == null || variableValue.isEmpty()) ? new ConstantExpressionWrapper(
                    null) : new VariableExpressionWrapper(variableValue, null);
            variableExpressions.add(new MapEntryExpressionWrapper(keyExpression, valueExpression, mapExpression));
        }
        mapExpression.setMapEntryExpressions(variableExpressions);
        return mapExpression;
    }

    public static boolean isCallTestCaseClass(ClassNodeWrapper classNode) {
        return (classNode.getName().equals(TestCase.class.getName()) || classNode.getName().equals(
                TestCase.class.getSimpleName()));
    }

    public static boolean isTestDataArgument(MethodCallExpressionWrapper objectMethodCallExpression) {
        return (objectMethodCallExpression.isObjectExpressionOfClass(TestDataFactory.class)
                && objectMethodCallExpression.getMethodAsString().equals(FIND_TEST_DATA_METHOD_NAME)
                && objectMethodCallExpression.getArguments() instanceof ArgumentListExpressionWrapper && ((ArgumentListExpressionWrapper) objectMethodCallExpression
                    .getArguments()).getExpressions().size() == 1);
    }

    public static ExpressionWrapper getTestDataObject(MethodCallExpressionWrapper methodCallExpression) {
        if (!isTestDataArgument(methodCallExpression)) {
            return null;
        }
        return ((ArgumentListExpressionWrapper) methodCallExpression.getArguments()).getExpression(0);
    }

    public static boolean setTestDataObject(MethodCallExpressionWrapper methodCallExpression, String testDataValue) {
        if (!isTestDataArgument(methodCallExpression)) {
            return false;
        }
        ((ConstantExpressionWrapper) ((ArgumentListExpressionWrapper) methodCallExpression.getArguments())
                .getExpression(0)).setValue(testDataValue);
        return true;
    }

    public static MethodCallExpressionWrapper getNewTestDataExpression(String testDataPk, ASTNodeWrapper parent) {
        MethodCallExpressionWrapper newMethodCall = new MethodCallExpressionWrapper(TestDataFactory.class,
                FIND_TEST_DATA_METHOD_NAME, parent);
        ArgumentListExpressionWrapper argumentExpressionWrapper = (ArgumentListExpressionWrapper) newMethodCall
                .getArguments();
        argumentExpressionWrapper.addExpression(new ConstantExpressionWrapper(testDataPk, argumentExpressionWrapper));
        return newMethodCall;
    }

    public static MethodCallExpressionWrapper getNewTestDataValueExpression(String testDataPk, Object columnValue,
            Object rowValue, ASTNodeWrapper parent) {
        MethodCallExpressionWrapper newMethodCall = new MethodCallExpressionWrapper(parent);
        newMethodCall.setObjectExpression(getNewTestDataExpression(testDataPk, newMethodCall));
        newMethodCall.setMethod(new ConstantExpressionWrapper(GET_VALUE_METHOD_NAME, newMethodCall));
        ArgumentListExpressionWrapper argumentExpressionWrapper = (ArgumentListExpressionWrapper) newMethodCall
                .getArguments();
        argumentExpressionWrapper.addExpression(new ConstantExpressionWrapper(columnValue, argumentExpressionWrapper));
        argumentExpressionWrapper.addExpression(new ConstantExpressionWrapper(rowValue, argumentExpressionWrapper));
        return newMethodCall;
    }

    public static boolean isTestDataValueArgument(MethodCallExpressionWrapper objectMethodCallExpression) {
        return (objectMethodCallExpression.getObjectExpression() instanceof MethodCallExpressionWrapper
                && AstEntityInputUtil.isTestDataArgument((MethodCallExpressionWrapper) objectMethodCallExpression
                        .getObjectExpression()) && objectMethodCallExpression.getMethodAsString().equals(
                GET_VALUE_METHOD_NAME));
    }

    public static ArgumentListExpressionWrapper getTestDataValueArgument(
            MethodCallExpressionWrapper methodCallExpression) {
        if (!isTestDataValueArgument(methodCallExpression)
                || !(methodCallExpression.getArguments() instanceof ArgumentListExpressionWrapper)) {
            return null;
        }
        return (ArgumentListExpressionWrapper) methodCallExpression.getArguments();
    }

    public static ExpressionWrapper getTestDataValueObject(MethodCallExpressionWrapper methodCallExpression) {
        if (isTestDataValueArgument(methodCallExpression)
                && methodCallExpression.getObjectExpression() instanceof MethodCallExpressionWrapper) {
            return getTestDataObject((MethodCallExpressionWrapper) methodCallExpression.getObjectExpression());
        }
        return null;
    }

    public static boolean isObjectArgument(MethodCallExpressionWrapper objectMethodCallExpression) {
        if (objectMethodCallExpression.isObjectExpressionOfClass(ObjectRepository.class)
                && objectMethodCallExpression.getArguments() instanceof ArgumentListExpressionWrapper) {
            return true;
        }
        return false;
    }

    public static ExpressionWrapper getObjectParam(MethodCallExpressionWrapper methodCallExpression) {
        if (!isObjectArgument(methodCallExpression)) {
            return null;
        }
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) methodCallExpression
                .getArguments();
        if (argumentList.getExpressions() != null && !argumentList.getExpressions().isEmpty()) {
            // return first item because find test object method only have one
            // parameter
            return argumentList.getExpression(0);
        }
        return null;
    }

    public static MethodCallExpressionWrapper generateObjectMethodCall(String objectPk, ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = new MethodCallExpressionWrapper(ObjectRepository.class,
                FIND_TEST_OBJECT_METHOD_NAME, parentNode);
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(objectMethodCall);
        argument.addExpression(new ConstantExpressionWrapper(objectPk, argument));
        objectMethodCall.setArguments(argument);
        return objectMethodCall;
    }

    public static boolean isObjectClass(ClassNodeWrapper classNode) {
        return (TestObject.class.getName().equals(classNode.getTypeClass().getName())
                || TestObject.class.getSimpleName().equals(classNode.getTypeClass().getSimpleName()) || TestObject.class
                    .isAssignableFrom(classNode.getTypeClass()));
    }

    public static boolean isObjectClass(ClassNode classNode) {
        return (TestObject.class.getName().equals(classNode.getTypeClass().getName())
                || TestObject.class.getSimpleName().equals(classNode.getTypeClass().getSimpleName()) || TestObject.class
                    .isAssignableFrom(classNode.getTypeClass()));
    }

    public static boolean isObjectClass(Class<?> clazz) {
        return (TestObject.class.getName().equals(clazz.getName())
                || TestObject.class.getSimpleName().equals(clazz.getSimpleName()) || TestObject.class
                    .isAssignableFrom(clazz));
    }

    public static String getTextValueForTestObjectArgument(MethodCallExpressionWrapper methodCall) {
        String pk = String.valueOf(((ConstantExpressionWrapper) ((ArgumentListExpressionWrapper) methodCall
                .getArguments()).getExpressions().get(0)).getValue());
        WebElementEntity webElement = null;
        try {
            webElement = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(pk);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (webElement != null) {
            return webElement.getName();
        }
        return pk;
    }

    public static String getTextValueForTestDataArgument(MethodCallExpressionWrapper methodCall) {
        String pk = String.valueOf(((ConstantExpressionWrapper) ((ArgumentListExpressionWrapper) methodCall
                .getArguments()).getExpressions().get(0)).getValue());
        DataFileEntity dataFile = null;
        try {
            dataFile = TestDataController.getInstance().getTestDataByDisplayId(pk);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (dataFile != null) {
            return dataFile.getName();
        }
        return pk;
    }

    public static String getTextValueForTestDataValueArgument(MethodCallExpressionWrapper methodCall) {
        StringBuilder result = new StringBuilder();
        if (methodCall.getObjectExpression() instanceof MethodCallExpressionWrapper) {
            result.append(getTextValueForTestDataArgument(((MethodCallExpressionWrapper) methodCall
                    .getObjectExpression())));
        }
        result.append(methodCall.getArguments().getText());
        return result.toString();
    }

    public static String getTextValueForTestCaseArgument(MethodCallExpressionWrapper methodCall) {
        String pk = String.valueOf(((ConstantExpressionWrapper) ((ArgumentListExpressionWrapper) methodCall
                .getArguments()).getExpressions().get(0)).getValue());
        TestCaseEntity testCase = null;
        try {
            testCase = TestCaseController.getInstance().getTestCaseByDisplayId(pk);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (testCase != null) {
            return testCase.getName();
        }
        return pk;
    }
}
