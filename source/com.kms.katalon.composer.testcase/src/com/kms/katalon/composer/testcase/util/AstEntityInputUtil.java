package com.kms.katalon.composer.testcase.util;

import static com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper.GET_VALUE_METHOD_NAME;
import static com.kms.katalon.constants.GlobalStringConstants.ENTITY_ID_SEPARATOR;

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
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.testobject.WindowsTestObject;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

/**
 * Utility class to help processing AST node for Katalon entities
 */
public class AstEntityInputUtil {
    public static MethodCallExpressionWrapper createNewFindTestCaseMethodCall(TestCaseEntity testCase,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = MethodCallExpressionWrapper
                .newLocalMethod(MethodCallExpressionWrapper.FIND_TEST_CASE_METHOD_NAME, parentNode);
             objectMethodCall.getArguments().addExpression(new ConstantExpressionWrapper(
             testCase != null ? TestCaseFactory.getTestCaseRelativeId(testCase.getIdForDisplay()) : null));
        return objectMethodCall;
    }

    public static boolean setTestCaseIdIntoFindTestCaseMethodCall(MethodCallExpressionWrapper methodCallExpression,
            String testCaseId) {
        if (!methodCallExpression.isFindTestCaseMethodCall()) {
            return false;
        }
        return setEntityIdToMethodCall(methodCallExpression, TestCaseFactory.getTestCaseRelativeId(testCaseId));
    }

    public static String findTestCaseIdArgumentFromFindTestCaseMethodCall(
            MethodCallExpressionWrapper methodCallExpression) {
        if (!methodCallExpression.isFindTestCaseMethodCall()) {
            return null;
        }
        return TestCaseFactory.getTestCaseId(getEntityRelativeIdFromMethodCall(methodCallExpression));
    }

    public static String findTestCaseIdArgumentFromCallTestCaseMethodCall(
            MethodCallExpressionWrapper methodCallExpression) {
        ExpressionWrapper firstArgumentEprs = methodCallExpression.getArguments().getExpression(0);
        if (!methodCallExpression.isCallTestCaseMethodCall()
                || !(firstArgumentEprs instanceof MethodCallExpressionWrapper)) {
            return null;
        }
        return findTestCaseIdArgumentFromFindTestCaseMethodCall((MethodCallExpressionWrapper) firstArgumentEprs);
    }

    public static String findCheckpointIdArgumentFromFindCheckpointMethodCall(
            MethodCallExpressionWrapper methodCallExpression) {
        if (!methodCallExpression.isFindCheckpointMethodCall()) {
            return null;
        }
        return getEntityRelativeIdFromMethodCall(methodCallExpression);
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
                defaultBuiltinKeywordContributor.getAliasName(), BuiltInMethodNodeFactory.CALL_TEST_CASE_METHOD_NAME,
                null);
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) keywordMethodCallExpressionWrapper
                .getArguments();
        argumentList.addExpression(createNewFindTestCaseMethodCall(testCase, argumentList));
        argumentList.addExpression(generateTestCaseVariableBindingMapExpression(testCase, argumentList));
        argumentList.addExpression(AstKeywordsInputUtil.getNewFailureHandlingPropertyExpression(argumentList));
        ExpressionStatementWrapper newCallTestCaseExpression = new ExpressionStatementWrapper(
                keywordMethodCallExpressionWrapper, parentNode);
        if (variablesToAdd != null) {
            variablesToAdd.addAll(getCallTestCaseVariables(keywordMethodCallExpressionWrapper));
        }
        return newCallTestCaseExpression;
    }

    public static List<VariableEntity> getCallTestCaseVariables(MethodCallExpressionWrapper callTestCaseMethodCall) {
        if (!callTestCaseMethodCall.isCallTestCaseMethodCall()
                || TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue()
                || !TestCasePreferenceDefaultValueInitializer.isSetAutoExportVariables()) {
            return Collections.emptyList();
        }
        ArgumentListExpressionWrapper argumentListExpressionWrapper = callTestCaseMethodCall.getArguments();
        if (!(argumentListExpressionWrapper.getExpression(0) instanceof MethodCallExpressionWrapper)
                || !(argumentListExpressionWrapper.getExpression(1) instanceof MapExpressionWrapper)
                || !((MethodCallExpressionWrapper) argumentListExpressionWrapper.getExpression(0))
                        .isFindTestCaseMethodCall()) {
            return Collections.emptyList();
        }
        String calledTestCaseId = AstEntityInputUtil.findTestCaseIdArgumentFromFindTestCaseMethodCall(
                (MethodCallExpressionWrapper) argumentListExpressionWrapper.getExpression(0));
        MapExpressionWrapper mapExpressionWrapper = (MapExpressionWrapper) argumentListExpressionWrapper
                .getExpression(1);
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
        return (classNode.getName().equals(TestCase.class.getName())
                || classNode.getName().equals(TestCase.class.getSimpleName()));
    }

    public static boolean isTestCaseClass(Class<?> clazz) {
        return (TestCase.class.getName().equals(clazz.getName())
                || TestCase.class.getSimpleName().equals(clazz.getSimpleName())
                || TestCase.class.isAssignableFrom(clazz));
    }

    public static String findTestDataIdFromFindTestDataMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        if (!methodCallExpression.isFindTestDataMethodCall()) {
            return null;
        }
        return TestDataFactory.getTestDataId(getEntityRelativeIdFromMethodCall(methodCallExpression));
    }

    public static boolean setTestDataIdIntoFindTestDataMethodCall(MethodCallExpressionWrapper methodCallExpression,
            String testDataValue) {
        if (!methodCallExpression.isFindTestDataMethodCall()) {
            return false;
        }
        return setEntityIdToMethodCall(methodCallExpression, TestDataFactory.getTestDataRelativeId(testDataValue));
    }

    private static boolean setEntityIdToMethodCall(MethodCallExpressionWrapper methodCallExpression,
            String entityRelativeId) {
        if (methodCallExpression == null || methodCallExpression.getArguments().getExpressions().size() == 0) {
            return false;
        }
        ExpressionWrapper expression = methodCallExpression.getArguments().getExpression(0);
        if (expression instanceof ConstantExpressionWrapper) {
            ((ConstantExpressionWrapper) expression).setValue(entityRelativeId);
            return true;
        }
        methodCallExpression.getArguments().setExpression(new ConstantExpressionWrapper(entityRelativeId), 0);
        return true;
    }

    public static MethodCallExpressionWrapper createNewFindTestDataExpression(String testDataId,
            ASTNodeWrapper parent) {
        MethodCallExpressionWrapper newMethodCall = MethodCallExpressionWrapper
                .newLocalMethod(MethodCallExpressionWrapper.FIND_TEST_DATA_METHOD_NAME, parent);
        newMethodCall.getArguments()
                .addExpression(new ConstantExpressionWrapper(TestDataFactory.getTestDataRelativeId(testDataId)));
        return newMethodCall;
    }

    public static MethodCallExpressionWrapper createNewGetTestDataValueExpression(String testDataId, Object columnValue,
            Object rowValue, ASTNodeWrapper parent) {
        MethodCallExpressionWrapper newMethodCall = new MethodCallExpressionWrapper(parent);
        newMethodCall.setObjectExpression(createNewFindTestDataExpression(testDataId, newMethodCall));
        newMethodCall.setMethod(new ConstantExpressionWrapper(GET_VALUE_METHOD_NAME, newMethodCall));
        ArgumentListExpressionWrapper argumentExpressionWrapper = (ArgumentListExpressionWrapper) newMethodCall
                .getArguments();
        argumentExpressionWrapper.addExpression(new ConstantExpressionWrapper(columnValue, argumentExpressionWrapper));
        argumentExpressionWrapper.addExpression(new ConstantExpressionWrapper(rowValue, argumentExpressionWrapper));
        return newMethodCall;
    }

    public static String findTestObjectIdFromFindTestObjectMethodCall(
            MethodCallExpressionWrapper methodCallExpression) {
        if (!methodCallExpression.isFindTestObjectMethodCall()) {
            return null;
        }
        return ObjectRepository.getTestObjectId(getEntityRelativeIdFromMethodCall(methodCallExpression));
    }
    
    public static String findWindowsTestObjectIdFromFindTestObjectMethodCall(
            MethodCallExpressionWrapper methodCallExpression) {
        if (!methodCallExpression.isFindWindowsObjectMethodCall()) {
            return null;
        }
        return ObjectRepository.getTestObjectId(getEntityRelativeIdFromMethodCall(methodCallExpression));
    }

    public static MethodCallExpressionWrapper createNewFindTestObjectMethodCall(ASTNodeWrapper parentNode) {
        return createNewFindTestObjectMethodCall(null, parentNode);
    }
    
    public static MethodCallExpressionWrapper createNewFindWindowsObjectMethodCall(ASTNodeWrapper parentNode) {
        return createNewFindWindowsObjectMethodCall(null, parentNode);
    }

    public static MethodCallExpressionWrapper createNewFindTestObjectMethodCall(String testObjectId,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = MethodCallExpressionWrapper
                .newLocalMethod(MethodCallExpressionWrapper.FIND_TEST_OBJECT_METHOD_NAME, parentNode);
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(objectMethodCall);
                argument.addExpression(
                new ConstantExpressionWrapper(ObjectRepository.getTestObjectRelativeId(testObjectId), argument));
        objectMethodCall.setArguments(argument);
        return objectMethodCall;
    }

    public static MethodCallExpressionWrapper createNewFindWindowsObjectMethodCall(String testObjectId,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = MethodCallExpressionWrapper
                .newLocalMethod(MethodCallExpressionWrapper.FIND_WINDOWS_OBJECT_METHOD_NAME, parentNode);
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(objectMethodCall);
                argument.addExpression(
                new ConstantExpressionWrapper(ObjectRepository.getTestObjectRelativeId(testObjectId), argument));
        objectMethodCall.setArguments(argument);
        return objectMethodCall;
    }

    public static MethodCallExpressionWrapper createNewFindCheckpointMethodCall(ASTNodeWrapper parentNode) {
        return createNewFindCheckpointMethodCall(null, parentNode);
    }

    public static MethodCallExpressionWrapper createNewFindCheckpointMethodCall(String checkpointId,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper objectMethodCall = MethodCallExpressionWrapper
                .newLocalMethod(MethodCallExpressionWrapper.FIND_CHECKPOINT_METHOD_NAME, parentNode);
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(objectMethodCall);
        argument.addExpression(new ConstantExpressionWrapper(checkpointId, argument));
        objectMethodCall.setArguments(argument);
        return objectMethodCall;
    }

    public static boolean setCheckpointIdIntoFindCheckpointMethodCall(MethodCallExpressionWrapper methodCallExpression,
            String checkpointValue) {
        if (!methodCallExpression.isFindCheckpointMethodCall()) {
            return false;
        }
        return setEntityIdToMethodCall(methodCallExpression, checkpointValue);
    }
    
    private static boolean isAssignFromObjectClass(Class<?> objectClass, Class<?> clazz) {
        if (objectClass == null || clazz == null) {
            return false;
        }

        return objectClass.getName().equals(clazz.getName())
        || objectClass.getSimpleName().equals(clazz.getSimpleName())
        || objectClass.isAssignableFrom(clazz);
    }

    public static boolean isTestObjectClass(ClassNodeWrapper classNode) {
        return isAssignFromObjectClass(TestObject.class, classNode.getTypeClass())
                || isAssignFromObjectClass(WindowsTestObject.class, classNode.getTypeClass());
    }

    public static boolean isTestObjectClass(ClassNode classNode) {
        return isAssignFromObjectClass(TestObject.class, classNode.getTypeClass())
                || isAssignFromObjectClass(WindowsTestObject.class, classNode.getTypeClass());
    }

    public static boolean isTestObjectClass(Class<?> clazz) {
        return isAssignFromObjectClass(TestObject.class, clazz)
                || isAssignFromObjectClass(WindowsTestObject.class, clazz);
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
                || FailureHandling.class.getSimpleName().equals(clazz.getSimpleName())
                || FailureHandling.class.isAssignableFrom(clazz));
    }

    public static String getEntityRelativeIdFromMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        if (methodCallExpression == null || methodCallExpression.getArguments().getExpressions().size() == 0) {
            return StringUtils.EMPTY;
        }
        ExpressionWrapper testCaseIdExpression = methodCallExpression.getArguments().getExpression(0);
        if (testCaseIdExpression instanceof ConstantExpressionWrapper) {
            return ((ConstantExpressionWrapper) testCaseIdExpression).getValueAsString();
        }
        return testCaseIdExpression.getText();
    }

    public static String getTextValueForTestObjectArgument(MethodCallExpressionWrapper methodCall) {
        return getTextValueForTestArtifaceArgument(methodCall);
    }

    public static String getTextValueForWindowsObjectArgument(MethodCallExpressionWrapper methodCall) {
        return getTextValueForTestArtifaceArgument(methodCall);
    }

    public static String getTextValueForTestDataArgument(MethodCallExpressionWrapper methodCall) {
        return getTextValueForTestArtifaceArgument(methodCall);
    }

    public static String getTextValueForTestCaseArgument(MethodCallExpressionWrapper methodCall) {
        return getTextValueForTestArtifaceArgument(methodCall);
    }

    private static String getTextValueForTestArtifaceArgument(MethodCallExpressionWrapper methodCall) {
        String relativeEntityId = getEntityRelativeIdFromMethodCall(methodCall);
        int lastIdSeparatorIdx = relativeEntityId.lastIndexOf(ENTITY_ID_SEPARATOR);
        if (lastIdSeparatorIdx <= 0) {
            return relativeEntityId;
        }
        return relativeEntityId.substring(lastIdSeparatorIdx + 1, relativeEntityId.length());
    }

    public static String getTextValueForTestDataValueArgument(MethodCallExpressionWrapper methodCall) {
        StringBuilder result = new StringBuilder();
        if (methodCall.getObjectExpression() instanceof MethodCallExpressionWrapper) {
            result.append(
                    getTextValueForTestDataArgument(((MethodCallExpressionWrapper) methodCall.getObjectExpression())));
        }
        result.append(methodCall.getArguments().getText());
        return result.toString();
    }

    public static String getTextValueForFindCheckpoint(MethodCallExpressionWrapper methodCall) {
        return getEntityRelativeIdFromMethodCall(methodCall).replaceFirst(
                GlobalStringConstants.ROOT_FOLDER_NAME_CHECKPOINT + ENTITY_ID_SEPARATOR, StringUtils.EMPTY);
    }
}
