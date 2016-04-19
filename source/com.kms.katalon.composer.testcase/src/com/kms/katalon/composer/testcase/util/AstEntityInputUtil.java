package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.kms.katalon.core.model.FailureHandling;
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

    public static final String BUILT_IN_KEYWORDS_CLASS_NAME_SUFFIX = "BuiltInKeywords";

    public static MethodCallExpressionWrapper createNewFindTestCaseMethodCall(TestCaseEntity testCase,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = new MethodCallExpressionWrapper(TestCaseFactory.class,
                FIND_TEST_CASE_METHOD_NAME, parentNode);
        objectMethodCall.getArguments().addExpression(
                new ConstantExpressionWrapper(testCase != null ? testCase.getIdForDisplay() : null));
        return objectMethodCall;
    }

    public static boolean isFindTestCaseMethodCall(MethodCallExpressionWrapper callTestCaseMethodCallExpression) {
        return (callTestCaseMethodCallExpression != null
                && callTestCaseMethodCallExpression.isObjectExpressionOfClass(TestCaseFactory.class) && callTestCaseMethodCallExpression.getMethodAsString()
                .equals(FIND_TEST_CASE_METHOD_NAME))
                && callTestCaseMethodCallExpression.getArguments().getExpressions().size() == 1;
    }

    public static boolean setTestCaseIdIntoFindTestCaseMethodCall(MethodCallExpressionWrapper methodCallExpression,
            String testCaseValue) {
        if (!isFindTestCaseMethodCall(methodCallExpression)) {
            return false;
        }
        return setEntityIdToMethodCall(methodCallExpression, testCaseValue);
    }

    public static boolean isCallTestCaseMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        return (methodCallExpression.getObjectExpressionAsString().endsWith(BUILT_IN_KEYWORDS_CLASS_NAME_SUFFIX)
                && methodCallExpression.getMethodAsString().equals(CALL_TEST_CASE_METHOD_NAME) && methodCallExpression.getArguments()
                .getExpressions()
                .size() > 1);
    }

    public static String findTestCaseIdArgumentFromFindTestCaseMethodCall(
            MethodCallExpressionWrapper methodCallExpression) {
        if (!isFindTestCaseMethodCall(methodCallExpression)) {
            return null;
        }
        return getEntityIdFromMethodCall(methodCallExpression);
    }

    public static String findTestCaseIdArgumentFromCallTestCaseMethodCall(
            MethodCallExpressionWrapper methodCallExpression) {
        if (!isCallTestCaseMethodCall(methodCallExpression)
                || !(methodCallExpression.getArguments().getExpression(0) instanceof MethodCallExpressionWrapper)) {
            return null;
        }
        return findTestCaseIdArgumentFromFindTestCaseMethodCall((MethodCallExpressionWrapper) methodCallExpression.getArguments()
                .getExpression(0));
    }

    public static ExpressionStatementWrapper generateCallTestCaseExpresionStatement(TestCaseEntity testCase,
            List<VariableEntity> variablesToAdd) {
        return generateCallTestCaseExpresionStatement(testCase, variablesToAdd, null);
    }

    public static ExpressionStatementWrapper generateCallTestCaseExpresionStatement(TestCaseEntity testCase,
            List<VariableEntity> variablesToAdd, ASTNodeWrapper parentNode) {
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
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) keywordMethodCallExpressionWrapper.getArguments();
        argumentList.addExpression(createNewFindTestCaseMethodCall(testCase, argumentList));
        argumentList.addExpression(generateTestCaseVariableBindingMapExpression(testCase, argumentList));
        argumentList.addExpression(AstKeywordsInputUtil.getNewFailureHandlingPropertyExpression(argumentList));

        if (variablesToAdd != null) {
            variablesToAdd.addAll(getCallTestCaseVariables(keywordMethodCallExpressionWrapper));
        }

        return new ExpressionStatementWrapper(keywordMethodCallExpressionWrapper, parentNode);
    }

    public static List<VariableEntity> getCallTestCaseVariables(MethodCallExpressionWrapper callTestCaseMethodCall) {
        if (!isCallTestCaseMethodCall(callTestCaseMethodCall)
                || TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue()
                || !TestCasePreferenceDefaultValueInitializer.isSetAutoExportVariables()) {
            return Collections.emptyList();
        }
        ArgumentListExpressionWrapper argumentListExpressionWrapper = callTestCaseMethodCall.getArguments();
        if (!(argumentListExpressionWrapper.getExpression(0) instanceof MethodCallExpressionWrapper)
                || !(argumentListExpressionWrapper.getExpression(1) instanceof MapExpressionWrapper)
                || !isFindTestCaseMethodCall((MethodCallExpressionWrapper) argumentListExpressionWrapper.getExpression(0))) {
            return Collections.emptyList();
        }
        String calledTestCaseId = AstEntityInputUtil.findTestCaseIdArgumentFromFindTestCaseMethodCall((MethodCallExpressionWrapper) argumentListExpressionWrapper.getExpression(0));
        MapExpressionWrapper mapExpressionWrapper = (MapExpressionWrapper) argumentListExpressionWrapper.getExpression(1);
        List<VariableEntity> variableEntities = new ArrayList<VariableEntity>();
        for (MapEntryExpressionWrapper entryExpressionWrapper : mapExpressionWrapper.getMapEntryExpressions()) {
            String variableName = (entryExpressionWrapper.getKeyExpression() instanceof ConstantExpressionWrapper)
                    ? ((ConstantExpressionWrapper) entryExpressionWrapper.getKeyExpression()).getValueAsString()
                    : entryExpressionWrapper.getKeyExpression().getText();

            VariableEntity variableInCalledTestCase = null;
            try {
                variableInCalledTestCase = TestCaseController.getInstance().getVariable(calledTestCaseId, variableName);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
            if (variableInCalledTestCase == null) {
                continue;
            }

            VariableEntity newVariable = new VariableEntity();
            newVariable.setName(variableName);
            newVariable.setDefaultValue(variableInCalledTestCase.getDefaultValue());
            variableEntities.add(newVariable);
        }
        return variableEntities;
    }

    public static MapExpressionWrapper generateTestCaseVariableBindingMapExpression(TestCaseEntity testCase,
            ASTNodeWrapper parent) {
        boolean generateDefaultValue = TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue();
        MapExpressionWrapper mapExpression = new MapExpressionWrapper(parent);
        for (VariableEntity variableEntity : testCase.getVariables()) {
            ConstantExpressionWrapper keyExpression = new ConstantExpressionWrapper(variableEntity.getName());
            String variableValue = variableEntity.getDefaultValue();
            if (!generateDefaultValue) {
                variableValue = variableEntity.getName();
            }
            ExpressionWrapper valueExpression = (variableValue == null || variableValue.isEmpty())
                    ? new ConstantExpressionWrapper() : new VariableExpressionWrapper(variableValue);
            mapExpression.addExpression(new MapEntryExpressionWrapper(keyExpression, valueExpression, mapExpression));
        }
        return mapExpression;
    }

    public static boolean isTestCaseClass(ClassNodeWrapper classNode) {
        return (classNode.getName().equals(TestCase.class.getName()) || classNode.getName().equals(
                TestCase.class.getSimpleName()));
    }

    public static boolean isTestCaseClass(Class<?> clazz) {
        return (TestCase.class.getName().equals(clazz.getName())
                || TestCase.class.getSimpleName().equals(clazz.getSimpleName()) || TestCase.class.isAssignableFrom(clazz));
    }

    public static boolean isFindTestDataMethodCall(MethodCallExpressionWrapper objectMethodCallExpression) {
        return (objectMethodCallExpression.isObjectExpressionOfClass(TestDataFactory.class)
                && StringUtils.equals(objectMethodCallExpression.getMethodAsString(), FIND_TEST_DATA_METHOD_NAME) && objectMethodCallExpression.getArguments()
                .getExpressions()
                .size() == 1);
    }

    public static String findTestDataIdFromFindTestDataMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        if (!isFindTestDataMethodCall(methodCallExpression)) {
            return null;
        }
        return getEntityIdFromMethodCall(methodCallExpression);
    }

    public static boolean setTestDataIdIntoFindTestDataMethodCall(MethodCallExpressionWrapper methodCallExpression,
            String testDataValue) {
        if (!isFindTestDataMethodCall(methodCallExpression)) {
            return false;
        }
        return setEntityIdToMethodCall(methodCallExpression, testDataValue);
    }

    private static boolean setEntityIdToMethodCall(MethodCallExpressionWrapper methodCallExpression, String entityValue) {
        if (methodCallExpression == null || methodCallExpression.getArguments().getExpressions().size() == 0) {
            return false;
        }
        ExpressionWrapper expression = methodCallExpression.getArguments().getExpression(0);
        if (expression instanceof ConstantExpressionWrapper) {
            ((ConstantExpressionWrapper) expression).setValue(entityValue);
            return true;
        }
        methodCallExpression.getArguments().setExpression(new ConstantExpressionWrapper(entityValue), 0);
        return true;
    }

    public static MethodCallExpressionWrapper createNewFindTestDataExpression(String testDataPk, ASTNodeWrapper parent) {
        MethodCallExpressionWrapper newMethodCall = new MethodCallExpressionWrapper(TestDataFactory.class,
                FIND_TEST_DATA_METHOD_NAME, parent);
        newMethodCall.getArguments().addExpression(new ConstantExpressionWrapper(testDataPk));
        return newMethodCall;
    }

    public static MethodCallExpressionWrapper createNewGetTestDataValueExpression(String testDataPk,
            Object columnValue, Object rowValue, ASTNodeWrapper parent) {
        MethodCallExpressionWrapper newMethodCall = new MethodCallExpressionWrapper(parent);
        newMethodCall.setObjectExpression(createNewFindTestDataExpression(testDataPk, newMethodCall));
        newMethodCall.setMethod(new ConstantExpressionWrapper(GET_VALUE_METHOD_NAME, newMethodCall));
        ArgumentListExpressionWrapper argumentExpressionWrapper = (ArgumentListExpressionWrapper) newMethodCall.getArguments();
        argumentExpressionWrapper.addExpression(new ConstantExpressionWrapper(columnValue, argumentExpressionWrapper));
        argumentExpressionWrapper.addExpression(new ConstantExpressionWrapper(rowValue, argumentExpressionWrapper));
        return newMethodCall;
    }

    public static boolean isGetTestDataValueMethodCall(MethodCallExpressionWrapper objectMethodCallExpression) {
        return (objectMethodCallExpression.getObjectExpression() instanceof MethodCallExpressionWrapper
                && AstEntityInputUtil.isFindTestDataMethodCall((MethodCallExpressionWrapper) objectMethodCallExpression.getObjectExpression()) && StringUtils.equals(
                objectMethodCallExpression.getMethodAsString(), GET_VALUE_METHOD_NAME));
    }

    public static boolean isFindTestObjectMethodCall(MethodCallExpressionWrapper objectMethodCallExpression) {
        if (objectMethodCallExpression.isObjectExpressionOfClass(ObjectRepository.class)
                && StringUtils.equals(objectMethodCallExpression.getMethodAsString(), FIND_TEST_OBJECT_METHOD_NAME)
                && objectMethodCallExpression.getArguments().getExpressions().size() == 1) {
            return true;
        }
        return false;
    }

    public static String findTestObjectIdFromFindTestObjectMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        if (!isFindTestObjectMethodCall(methodCallExpression)) {
            return null;
        }
        return getEntityIdFromMethodCall(methodCallExpression);
    }

    public static MethodCallExpressionWrapper createNewFindTestObjectMethodCall(ASTNodeWrapper parentNode) {
        return createNewFindTestObjectMethodCall(null, parentNode);
    }

    public static MethodCallExpressionWrapper createNewFindTestObjectMethodCall(String objectPk,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = new MethodCallExpressionWrapper(ObjectRepository.class,
                FIND_TEST_OBJECT_METHOD_NAME, parentNode);
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(objectMethodCall);
        argument.addExpression(new ConstantExpressionWrapper(objectPk, argument));
        objectMethodCall.setArguments(argument);
        return objectMethodCall;
    }

    public static boolean isTestObjectClass(ClassNodeWrapper classNode) {
        return (TestObject.class.getName().equals(classNode.getTypeClass().getName())
                || TestObject.class.getSimpleName().equals(classNode.getTypeClass().getSimpleName()) || TestObject.class.isAssignableFrom(classNode.getTypeClass()));
    }

    public static boolean isTestObjectClass(ClassNode classNode) {
        return (TestObject.class.getName().equals(classNode.getTypeClass().getName())
                || TestObject.class.getSimpleName().equals(classNode.getTypeClass().getSimpleName()) || TestObject.class.isAssignableFrom(classNode.getTypeClass()));
    }

    public static boolean isTestObjectClass(Class<?> clazz) {
        return (TestObject.class.getName().equals(clazz.getName())
                || TestObject.class.getSimpleName().equals(clazz.getSimpleName()) || TestObject.class.isAssignableFrom(clazz));
    }

    public static boolean isClassChildOf(String parentClassName, String childClassName) {
        try {
            return Class.forName(parentClassName).isAssignableFrom(Class.forName(childClassName));
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static boolean isFailureHandlingClass(Class<?> clazz) {
        return (FailureHandling.class.getName().equals(clazz.getName())
                || FailureHandling.class.getSimpleName().equals(clazz.getSimpleName()) || FailureHandling.class.isAssignableFrom(clazz));
    }

    private static String getEntityIdFromMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        if (methodCallExpression == null || methodCallExpression.getArguments().getExpressions().size() == 0) {
            return "";
        }
        ExpressionWrapper testCaseIdExpression = methodCallExpression.getArguments().getExpression(0);
        if (testCaseIdExpression instanceof ConstantExpressionWrapper) {
            return ((ConstantExpressionWrapper) testCaseIdExpression).getValueAsString();
        }
        return testCaseIdExpression.getText();
    }

    public static String getTextValueForTestObjectArgument(MethodCallExpressionWrapper methodCall) {
        String idString = getEntityIdFromMethodCall(methodCall);
        WebElementEntity webElement = null;
        try {
            webElement = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(idString);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (webElement != null) {
            return webElement.getName();
        }
        return idString;
    }

    public static String getTextValueForTestDataArgument(MethodCallExpressionWrapper methodCall) {
        String idString = getEntityIdFromMethodCall(methodCall);
        DataFileEntity dataFile = null;
        try {
            dataFile = TestDataController.getInstance().getTestDataByDisplayId(idString);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (dataFile != null) {
            return dataFile.getName();
        }
        return idString;
    }

    public static String getTextValueForTestCaseArgument(MethodCallExpressionWrapper methodCall) {
        String idString = getEntityIdFromMethodCall(methodCall);
        TestCaseEntity testCase = null;
        try {
            testCase = TestCaseController.getInstance().getTestCaseByDisplayId(idString);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (testCase != null) {
            return testCase.getName();
        }
        return idString;
    }

    public static String getTextValueForTestDataValueArgument(MethodCallExpressionWrapper methodCall) {
        StringBuilder result = new StringBuilder();
        if (methodCall.getObjectExpression() instanceof MethodCallExpressionWrapper) {
            result.append(getTextValueForTestDataArgument(((MethodCallExpressionWrapper) methodCall.getObjectExpression())));
        }
        result.append(methodCall.getArguments().getText());
        return result.toString();
    }
}
