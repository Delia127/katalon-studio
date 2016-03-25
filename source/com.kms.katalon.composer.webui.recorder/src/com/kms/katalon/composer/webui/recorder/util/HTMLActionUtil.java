package com.kms.katalon.composer.webui.recorder.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
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
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class HTMLActionUtil {
    private static List<HTMLSynchronizeAction> synchronizeActions = null;
    private static List<HTMLValidationAction> validationActions = null;

    public static Statement generateWebUiTestStep(HTMLActionMapping actionMapping, WebElementEntity createdTestObject)
            throws Exception {
        Class<?> keywordClass = Class.forName(actionMapping.getAction().getMappedKeywordClassName());
        Method method = null;
        for (Method declareMethod : keywordClass.getMethods()) {
            if (declareMethod.getName().equals(actionMapping.getAction().getMappedKeywordMethod())) {
                method = declareMethod;
                break;
            }
        }
        if (method != null) {
            int actionDataCount = 0;
            List<Expression> arguments = new ArrayList<Expression>();
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                Class<?> argumentClass = method.getParameterTypes()[i];
                Expression generatedExression = null;
                if (argumentClass.getName().equals(TestObject.class.getName())) {
                    generatedExression = generateObjectMethodCall((createdTestObject != null) ? createdTestObject
                            .getIdForDisplay() : null);
                } else if (argumentClass.getName().equals(FailureHandling.class.getName())) {
                    generatedExression = generateFailureHandlingExpression();
                } else {
                    Object data = actionMapping.getData()[actionDataCount];
                    generatedExression = new ConstantExpression(data);
                    actionDataCount++;
                }
                arguments.add(generatedExression);
            }
            ArgumentListExpression argumentListExpression = new ArgumentListExpression(arguments);
            MethodCallExpression methodCallExpression = new MethodCallExpression(new VariableExpression(actionMapping
                    .getAction().getMappedKeywordClassSimpleName()),
                    actionMapping.getAction().getMappedKeywordMethod(), argumentListExpression);
            return new ExpressionStatement(methodCallExpression);
        }
        return null;
    }

    private static MethodCallExpression generateObjectMethodCall(String objectPk) {
        List<Expression> expressionArguments = new ArrayList<Expression>();
        expressionArguments.add(new ConstantExpression(objectPk));
        MethodCallExpression objectMethodCall = new MethodCallExpression(new VariableExpression(
                ObjectRepository.class.getSimpleName()), "find" + TestObject.class.getSimpleName(),
                new ArgumentListExpression(expressionArguments));
        return objectMethodCall;
    }

    public static Expression generateFailureHandlingExpression() {
        return AstTreeTableInputUtil.createPropertyExpressionForClass(FailureHandling.class.getSimpleName(),
                TestCasePreferenceDefaultValueInitializer.getDefaultFailureHandling().name());
    }

    public static boolean verifyActionMapping(HTMLActionMapping actionMapping,
            List<HTMLActionMapping> existingActionMappings) {
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

    public static HTMLActionParam[] collectKeywordParam(String keywordClass, String keywordMethod) {
        List<HTMLActionParam> paramList = new ArrayList<HTMLActionParam>();
        try {
            Method actionMethod = null;
            for (Method method : Class.forName(keywordClass).getMethods()) {
                if (method.getName().equals(keywordMethod)) {
                    actionMethod = method;
                    break;
                }
            }
            if (actionMethod != null) {
                List<String> paramNames = KeywordController.getInstance().getParameterName(actionMethod);
                for (int i = 0; i < paramNames.size() && i < actionMethod.getParameterTypes().length; i++) {
                    if (actionMethod.getParameterTypes()[i] == TestObject.class
                            || actionMethod.getParameterTypes()[i] == FailureHandling.class) {
                        continue;
                    }
                    paramList.add(new HTMLActionParam(paramNames.get(i), actionMethod.getParameterTypes()[i]));
                }
            }
        } catch (Exception e) {
            // Cannot find method
            LoggerSingleton.logError(e);
        }
        return paramList.toArray(new HTMLActionParam[paramList.size()]);
    }

    public static boolean hasElement(String keywordClass, String keywordMethod) {
        try {
            Method actionMethod = null;
            for (Method method : Class.forName(keywordClass).getMethods()) {
                if (method.getName().equals(keywordMethod)) {
                    actionMethod = method;
                    break;
                }
            }
            if (actionMethod != null) {
                List<String> paramNames = KeywordController.getInstance().getParameterName(actionMethod);
                for (int i = 0; i < paramNames.size() && i < actionMethod.getParameterTypes().length; i++) {
                    if (actionMethod.getParameterTypes()[i] == TestObject.class) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // Cannot find method
            LoggerSingleton.logError(e);
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
            if (existingParamData != null
                    && action.getParams()[i].getClazz().isAssignableFrom(existingParamData.getClass())) {
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
