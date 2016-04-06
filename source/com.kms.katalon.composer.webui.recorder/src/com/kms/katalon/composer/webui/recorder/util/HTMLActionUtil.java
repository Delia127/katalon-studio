package com.kms.katalon.composer.webui.recorder.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLSynchronizeAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLValidationAction;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction.HTMLActionParam;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionJsonParser.HTMLActionJson;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class HTMLActionUtil {
    private static List<HTMLSynchronizeAction> synchronizeActions = null;
    private static List<HTMLValidationAction> validationActions = null;

    public static StatementWrapper generateWebUiTestStep(HTMLActionMapping actionMapping, WebElementEntity createdTestObject)
            throws Exception {
        Class<?> keywordClass = Class.forName(actionMapping.getAction().getMappedKeywordClassName());
        Method method = null;
        for (Method declareMethod : keywordClass.getMethods()) {
            if (declareMethod.getName().equals(actionMapping.getAction().getMappedKeywordMethod())) {
                method = declareMethod;
                break;
            }
        }
        if (method == null) {
            return null;
        }
        int actionDataCount = 0;

        MethodCallExpressionWrapper methodCallExpressionWrapper = new MethodCallExpressionWrapper(actionMapping.getAction()
                .getMappedKeywordClassSimpleName(), actionMapping.getAction().getMappedKeywordMethod(), null);
        ArgumentListExpressionWrapper argumentListExpressionWrapper = (ArgumentListExpressionWrapper) methodCallExpressionWrapper
                .getArguments();
        List<ExpressionWrapper> arguments = argumentListExpressionWrapper.getExpressions();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> argumentClass = method.getParameterTypes()[i];
            ExpressionWrapper generatedExression = null;
            if (argumentClass.getName().equals(TestObject.class.getName())) {
                generatedExression = AstEntityInputUtil.generateObjectMethodCall(
                        (createdTestObject != null) ? createdTestObject.getIdForDisplay() : null, null);
            } else if (argumentClass.getName().equals(FailureHandling.class.getName())) {
                generatedExression = AstTreeTableInputUtil.getNewFailureHandlingPropertyExpression(null);
            } else {
                Object data = actionMapping.getData()[actionDataCount];
                generatedExression = new ConstantExpressionWrapper(data, argumentListExpressionWrapper);
                actionDataCount++;
            }
            arguments.add(generatedExression);
        }
        return new ExpressionStatementWrapper(methodCallExpressionWrapper, null);
    }

    public static boolean verifyActionMapping(HTMLActionMapping actionMapping, List<HTMLActionMapping> existingActionMappings) {
        if (actionMapping == null || actionMapping.getAction() == null) {
            return false;
        }
        if (actionMapping.getAction() == HTMLAction.Navigate && existingActionMappings.size() > 0) {
            return false;
        }
        if (actionMapping.getAction().getName().equals(HTMLActionJson.DOUBLE_CLICK_ACTION_KEY)
                && existingActionMappings.size() >= 2) {
            HTMLActionMapping actionOffset_1 = existingActionMappings.get(existingActionMappings.size() - 1);
            HTMLActionMapping actionOffset_2 = existingActionMappings.get(existingActionMappings.size() - 2);
            if (actionOffset_1.getAction().getName().equals(HTMLActionJson.MOUSE_CLICK_ACTION_KEY)
                    && actionOffset_2.getAction().getName().equals(HTMLActionJson.MOUSE_CLICK_ACTION_KEY)
                    && actionOffset_1.getTargetElement().equals(actionMapping.getTargetElement())
                    && actionOffset_2.getTargetElement().equals(actionMapping.getTargetElement())) {
                existingActionMappings.remove(actionOffset_1);
                existingActionMappings.remove(actionOffset_2);
            }
        }
        return true;
    }

    public static HTMLActionMapping createNewSwitchToWindowAction(String windowTitle) {
        return new HTMLActionMapping(HTMLAction.SwitchToWindow, new String[] { windowTitle }, null);

    }

    public static String getPageTitleForAction(HTMLActionMapping actionMapping) {
        HTMLElement element = actionMapping.getTargetElement();
        while (!(element == null) && !(element instanceof HTMLPageElement)) {
            element = element.getParentElement();
        }
        if (element != null) {
            return ((HTMLPageElement) element).getAttributes().get(HTMLElementUtil.PAGE_TITLE_KEY);
        }
        return null;
    }

    public static List<IHTMLAction> getAllHTMLActions() {
        List<IHTMLAction> result = new ArrayList<IHTMLAction>();
        for (HTMLAction htmlAction : HTMLAction.values()) {
            result.add(htmlAction);
        }
        return result;
    }

    public static List<HTMLValidationAction> getAllHTMLValidationActions() {
        if (validationActions != null) {
            return validationActions;
        }
        validationActions = new ArrayList<HTMLValidationAction>();
        for (Method method : WebUiBuiltInKeywords.class.getDeclaredMethods()) {
            if (!method.getName().startsWith(HTMLValidationAction.VALIDATION_ACTION_PREFIX)) {
                continue;
            }
            validationActions.add(new HTMLValidationAction(method.getName(), WebUiBuiltInKeywords.class.getName(),
                    WebUiBuiltInKeywords.class.getSimpleName(), method.getName(), TestCaseEntityUtil
                            .getKeywordJavaDocText(WebUiBuiltInKeywords.class.getName(), method.getName(), null)));
        }
        return validationActions;
    }

    public static List<HTMLSynchronizeAction> getAllHTMLSynchronizeActions() {
        if (synchronizeActions != null) {
            return synchronizeActions;
        }
        synchronizeActions = new ArrayList<HTMLSynchronizeAction>();
        for (Method method : WebUiBuiltInKeywords.class.getDeclaredMethods()) {
            if (!method.getName().startsWith(HTMLSynchronizeAction.SYNCHRONIZE_ACTION_PREFIX)) {
                continue;
            }
            synchronizeActions.add(new HTMLSynchronizeAction(method.getName(), WebUiBuiltInKeywords.class.getName(),
                    WebUiBuiltInKeywords.class.getSimpleName(), method.getName(), TestCaseEntityUtil
                            .getKeywordJavaDocText(WebUiBuiltInKeywords.class.getName(), method.getName(), null)));
        }
        return synchronizeActions;
    }

    public static HTMLSynchronizeAction getDefaultSynchronizeAction() {
        List<HTMLSynchronizeAction> allActions = getAllHTMLSynchronizeActions();
        if (allActions == null) {
            return null;
        }
        for (HTMLSynchronizeAction action : allActions) {
            if (action.getName().equals("waitForElementPresent")) {
                return action;
            }
        }
        return (allActions.size() > 0) ? allActions.get(allActions.size() - 1) : null;
    }

    public static HTMLValidationAction getDefaultValidationAction() {
        List<HTMLValidationAction> allActions = getAllHTMLValidationActions();
        if (allActions == null) {
            return null;
        }
        for (HTMLValidationAction action : allActions) {
            if (action.getName().equals("verifyElementPresent")) {
                return action;
            }
        }
        return (allActions.size() > 0) ? allActions.get(allActions.size() - 1) : null;
    }

    public static HTMLActionParam[] collectKeywordParam(String keywordClass, String keywordMethodName) {
        List<HTMLActionParam> paramList = new ArrayList<HTMLActionParam>();
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(keywordClass, keywordMethodName);
        if (keywordMethod == null) {
            return paramList.toArray(new HTMLActionParam[paramList.size()]);
        }
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (keywordMethod.getParameters()[i].getType().getName().equals(TestObject.class.getName())
                    || keywordMethod.getParameters()[i].getType().getName().equals(FailureHandling.class.getName())) {
                continue;
            }
            paramList.add(new HTMLActionParam(keywordMethod.getParameters()[i].getName(), keywordMethod.getParameters()[i]
                    .getType()));
        }
        return paramList.toArray(new HTMLActionParam[paramList.size()]);
    }

    public static boolean hasElement(String keywordClass, String keywordMethodName) {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(keywordClass, keywordMethodName);
        if (keywordMethod == null) {
            return false;
        }
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (keywordMethod.getParameters()[i].getType().getName().equals(TestObject.class.getName())) {
                return true;
            }
        }
        return false;
    }

    public static Object[] generateParamDatas(IHTMLAction action, Object[] existingParamDatas) {
        if (action == null || action.getParams() == null) {
            return new Object[] {};
        }

        Object[] newParamDatas = new Object[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            Object existingParamData = (existingParamDatas != null && i < existingParamDatas.length) ? existingParamDatas[i]
                    : null;
            if (existingParamData != null && action.getParams()[i].getClazz().isAssignableFrom(existingParamData.getClass())) {
                newParamDatas[i] = existingParamData;
            } else {
                if (ClassUtils.isAssignable(action.getParams()[i].getClazz(), Number.class, true)) {
                    newParamDatas[i] = 0;
                } else if (ClassUtils.isAssignable(action.getParams()[i].getClazz(), Boolean.class, true)) {
                    newParamDatas[i] = false;
                } else if (ClassUtils.isAssignable(action.getParams()[i].getClazz(), String.class, true)) {
                    newParamDatas[i] = "";
                } else {
                    newParamDatas[i] = null;
                }
            }
        }
        return newParamDatas;
    }
}
