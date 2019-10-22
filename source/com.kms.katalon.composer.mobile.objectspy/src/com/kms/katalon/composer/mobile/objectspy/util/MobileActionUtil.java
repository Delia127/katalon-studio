package com.kms.katalon.composer.mobile.objectspy.util;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.mobile.objectspy.actions.IMobileAction;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionParam;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionParamValueType;
import com.kms.katalon.composer.mobile.objectspy.types.MobileElementMethodCallWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;
import com.kms.katalon.entity.repository.WebElementEntity;

public class MobileActionUtil {
    public static KeywordClass getMobileKeywordClass() {
        return KeywordController.getInstance().getBuiltInKeywordClassByName(MobileBuiltInKeywords.class.getName());
    }

    public static MobileActionParam[] collectKeywordParam(String keywordClass, String keywordMethodName) {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(keywordClass,
                keywordMethodName);
        if (keywordMethod == null) {
            return new MobileActionParam[0];
        }

        List<MobileActionParam> paramList = new ArrayList<MobileActionParam>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter parameter = keywordMethod.getParameters()[i];
            if (parameter.isGeneralParam()) {
                paramList.add(new MobileActionParam(parameter.getName(), parameter.getType()));
            }
        }
        return paramList.toArray(new MobileActionParam[paramList.size()]);
    }

    public static boolean hasElement(String keywordClass, String keywordMethodName) {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(keywordClass,
                keywordMethodName);
        if (keywordMethod == null) {
            return false;
        }

        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (keywordMethod.getParameters()[i].isTestObjectParam()) {
                return true;
            }
        }
        return false;
    }

    public static MobileActionParamValueType[] generateParamDatas(IMobileAction action,
            MobileActionParamValueType[] existingParamDatas) {
        if (action == null || action.getParams() == null) {
            return new MobileActionParamValueType[0];
        }

        MobileActionParamValueType[] newParamDataArray = new MobileActionParamValueType[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            MobileActionParamValueType existingParamData = (existingParamDatas != null && i < existingParamDatas.length)
                    ? existingParamDatas[i] : null;

            MobileActionParam mobileActionParam = action.getParams()[i];
            if (!isAssignableFromScript(existingParamData, mobileActionParam)) {
                InputValueEditorProvider valueType = AstInputValueTypeOptionsProvider
                        .getAssignableValueType(mobileActionParam.getClazz());
                if (valueType != null) {
                    existingParamData = MobileActionParamValueType.newInstance(valueType, mobileActionParam.getName());
                }
            }
            newParamDataArray[i] = existingParamData;
        }
        return newParamDataArray;
    }

    private static boolean isAssignableFromScript(MobileActionParamValueType existingParamData,
            MobileActionParam param) {
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

    private static KeywordMethod getMethodInActionMapping(MobileActionMapping actionMapping)
            throws ClassNotFoundException {
        IMobileAction action = actionMapping.getAction();
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

    public static StatementWrapper generateMobileTestStep(MobileActionMapping actionMapping,
            WebElementEntity createdTestObject, ASTNodeWrapper parentClassNode) throws ClassNotFoundException {
        KeywordMethod method = getMethodInActionMapping(actionMapping);
        if (method == null) {
            return null;
        }
        int actionDataCount = 0;

        IMobileAction action = actionMapping.getAction();
        MethodCallExpressionWrapper methodCallExpressionWrapper = new MethodCallExpressionWrapper(
                getMobileKeywordClass().getAliasName(), action.getMappedKeywordMethod(), parentClassNode);
        ArgumentListExpressionWrapper argumentListExpressionWrapper = methodCallExpressionWrapper.getArguments();
        for (int i = 0; i < method.getParameters().length; i++) {
            Class<?> argumentClass = method.getParameters()[i].getType();
            ExpressionWrapper generatedExression = null;
            if (argumentClass.getName().equals(TestObject.class.getName())) {
                generatedExression = new MobileElementMethodCallWrapper(parentClassNode, actionMapping.getTargetElement());
//                generatedExression = AstEntityInputUtil.createNewFindTestObjectMethodCall(
//                        (createdTestObject != null) ? createdTestObject.getIdForDisplay() : null, parentClassNode);
            } else if (argumentClass.getName().equals(FailureHandling.class.getName())) {
                generatedExression = AstKeywordsInputUtil.getNewFailureHandlingPropertyExpression(null);
            } else {
                MobileActionParamValueType paramValueType = actionMapping.getData()[actionDataCount];
                generatedExression = paramValueType.toExpressionWrapper();
                actionDataCount++;
            }
            argumentListExpressionWrapper.addExpression(generatedExression);
        }
        return new ExpressionStatementWrapper(methodCallExpressionWrapper, null);
    }
}
