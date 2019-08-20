package com.kms.katalon.composer.windows.action;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.windows.record.WindowsElementMethodCallWrapper;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.WindowsTestObject;
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class WindowsActionUtil {
    public static KeywordClass getWindowsKeywordClass() {
        return KeywordController.getInstance().getBuiltInKeywordClassByName(WindowsBuiltinKeywords.class.getName());
    }

    public static WindowsActionParam[] collectKeywordParam(String keywordClass, String keywordMethodName) {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(keywordClass,
                keywordMethodName);
        if (keywordMethod == null) {
            return new WindowsActionParam[0];
        }

        List<WindowsActionParam> paramList = new ArrayList<WindowsActionParam>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter parameter = keywordMethod.getParameters()[i];
            if (parameter.isGeneralParam()) {
                paramList.add(new WindowsActionParam(parameter.getName(), parameter.getType()));
            }
        }
        return paramList.toArray(new WindowsActionParam[paramList.size()]);
    }

    public static boolean hasElement(String keywordClass, String keywordMethodName) {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(keywordClass,
                keywordMethodName);
        if (keywordMethod == null) {
            return false;
        }

        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (keywordMethod.getParameters()[i].isWindowsTestObjectParam()) {
                return true;
            }
        }
        return false;
    }

    public static WindowsActionParamValueType[] generateParamDatas(IWindowsAction action,
            WindowsActionParamValueType[] existingParamDatas) {
        if (action == null || action.getParams() == null) {
            return new WindowsActionParamValueType[0];
        }

        WindowsActionParamValueType[] newParamDataArray = new WindowsActionParamValueType[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            WindowsActionParamValueType existingParamData = (existingParamDatas != null && i < existingParamDatas.length)
                    ? existingParamDatas[i] : null;

            WindowsActionParam mobileActionParam = action.getParams()[i];
            if (!isAssignableFromScript(existingParamData, mobileActionParam)) {
                InputValueEditorProvider valueType = AstInputValueTypeOptionsProvider
                        .getAssignableValueType(mobileActionParam.getClazz());
                if (valueType != null) {
                    existingParamData = WindowsActionParamValueType.newInstance(valueType, mobileActionParam.getName());
                }
            }
            newParamDataArray[i] = existingParamData;
        }
        return newParamDataArray;
    }

    private static boolean isAssignableFromScript(WindowsActionParamValueType existingParamData,
            WindowsActionParam param) {
        if (existingParamData == null) {
            return false;
        }

        ExpressionWrapper expression = existingParamData.toExpressionWrapper();

        if (expression == null) {
            return false;
        }

        Class<?> paramClass = param.getClazz();
        ClassNodeWrapper existingParamClassNode = expression.getType();
        if (paramClass.isPrimitive() || existingParamClassNode.getTypeClass() == null) {
            return paramClass.getName().equalsIgnoreCase(existingParamClassNode.getName());
        }
        return paramClass.isAssignableFrom(existingParamClassNode.getTypeClass());
    }

    private static KeywordMethod getMethodInActionMapping(WindowsActionMapping actionMapping)
            throws ClassNotFoundException {
        IWindowsAction action = actionMapping.getAction();
        if (action == null) {
            return null;
        }

        KeywordMethod method = null;
        for (KeywordMethod declareMethod : KeywordController.getInstance()
                .getBuiltInKeywords(action.getMappedKeywordClassSimpleName())) {
            if (declareMethod.getName().equals(action.getMappedKeywordMethod())) {
                method = declareMethod;
                break;
            }
        }
        return method;
    }

    public static StatementWrapper generateMobileTestStep(WindowsActionMapping actionMapping,
            WindowsElementEntity createdTestObject, ASTNodeWrapper parentClassNode) throws ClassNotFoundException {
        KeywordMethod method = getMethodInActionMapping(actionMapping);
        if (method == null) {
            return null;
        }
        int actionDataCount = 0;

        IWindowsAction action = actionMapping.getAction();
        MethodCallExpressionWrapper methodCallExpressionWrapper = new MethodCallExpressionWrapper(
                getWindowsKeywordClass().getAliasName(), action.getMappedKeywordMethod(), parentClassNode);
        ArgumentListExpressionWrapper argumentListExpressionWrapper = methodCallExpressionWrapper.getArguments();
        for (int i = 0; i < method.getParameters().length; i++) {
            Class<?> argumentClass = method.getParameters()[i].getType();
            ExpressionWrapper generatedExression = null;
            if (argumentClass.getName().equals(WindowsTestObject.class.getName())) {
                generatedExression = new WindowsElementMethodCallWrapper(parentClassNode, actionMapping.getTargetElement());
            } else if (argumentClass.getName().equals(FailureHandling.class.getName())) {
                generatedExression = AstKeywordsInputUtil.getNewFailureHandlingPropertyExpression(null);
            } else {
                WindowsActionParamValueType paramValueType = actionMapping.getData()[actionDataCount];
                generatedExression = paramValueType.toExpressionWrapper();
                actionDataCount++;
            }
            argumentListExpressionWrapper.addExpression(generatedExression);
        }
        return new ExpressionStatementWrapper(methodCallExpressionWrapper, null);
    }
}
