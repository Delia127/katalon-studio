package com.kms.katalon.composer.webui.recorder.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamValueType;
import com.kms.katalon.composer.webui.recorder.action.HTMLSynchronizeAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLValidationAction;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction.HTMLActionParam;
import com.kms.katalon.composer.webui.recorder.ast.RecordedElementMethodCallWrapper;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class HTMLActionUtil {
    private static final String ABOUT_BLANK = "\"about:blank\"";

    public static final int DF_SELECTED_INDEX_IF_NULL = 0;

    private static List<HTMLValidationAction> validationActions;

    private static List<HTMLSynchronizeAction> synchronizeActions;

    public static KeywordClass getWebUiKeywordClass() {
        return KeywordController.getInstance().getBuiltInKeywordClassByName(WebUiBuiltInKeywords.class.getName());
    }

    private static KeywordMethod getMethodInActionMapping(HTMLActionMapping actionMapping)
            throws ClassNotFoundException {
        IHTMLAction action = actionMapping.getAction();
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

    public static StatementWrapper generateWebUiTestStep(HTMLActionMapping actionMapping, WebElement webElement,
            ASTNodeWrapper parentClassNode) throws ClassNotFoundException {
        KeywordMethod method = getMethodInActionMapping(actionMapping);
        if (method == null) {
            return null;
        }
        int actionDataCount = 0;

        IHTMLAction action = actionMapping.getAction();
        MethodCallExpressionWrapper methodCallExpressionWrapper = new MethodCallExpressionWrapper(
                getWebUiKeywordClass().getAliasName(), action.getMappedKeywordMethod(), parentClassNode);
        ArgumentListExpressionWrapper argumentListExpressionWrapper = methodCallExpressionWrapper.getArguments();
        for (int i = 0; i < method.getParameters().length; i++) {
            Class<?> argumentClass = method.getParameters()[i].getType();
            ExpressionWrapper generatedExression = null;
            if (argumentClass.getName().equals(TestObject.class.getName())) {
                generatedExression = new RecordedElementMethodCallWrapper(parentClassNode,
                        webElement);
            } else if (argumentClass.getName().equals(FailureHandling.class.getName())) {
                generatedExression = AstKeywordsInputUtil.getNewFailureHandlingPropertyExpression(null);
            } else {
                HTMLActionParamValueType paramValueType = actionMapping.getData()[actionDataCount];
                generatedExression = paramValueType.toExpressionWrapper();
                actionDataCount++;
            }
            argumentListExpressionWrapper.addExpression(generatedExression);
        }
        return new ExpressionStatementWrapper(methodCallExpressionWrapper, null);
    }

    @SuppressWarnings("unused")
    private static boolean areElementsEqual(WebElement elm1, WebElement elm2) {
        return new EqualsBuilder().append(elm1.getType(), elm2.getType())
                .append(elm1.getTag(), elm2.getTag())
                .append(elm1.hasProperty(), elm2.hasProperty())
                .append(elm1.getXpath(), elm2.getXpath())
                .isEquals();
    }

    public static HTMLActionMapping createNewSwitchToWindowAction(String windowTitle) {
        HTMLActionParamValueType paramType = HTMLActionParamValueType.newInstance(InputValueType.String,
                convertToExpressionWrapper(GroovyStringUtil.toGroovyStringFormat(windowTitle)));
        return new HTMLActionMapping(HTMLAction.SwitchToWindow, new HTMLActionParamValueType[] { paramType }, null);

    }

    public static String getPageTitleForAction(HTMLActionMapping actionMapping) {
        WebElement element = actionMapping.getTargetElement();
        while (!(element == null) && !(element instanceof WebPage)) {
            element = element.getParent();
        }
        if (element != null) {
            WebElementPropertyEntity pageTitle = element.getProperty(HTMLElementUtil.PAGE_TITLE_KEY);
            if (pageTitle != null) {
                return pageTitle.getValue();
            }
        }
        return StringUtils.EMPTY;
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
                    WebUiBuiltInKeywords.class.getSimpleName(), method.getName(),
                    TestCaseEntityUtil.getKeywordJavaDocText(WebUiBuiltInKeywords.class.getName(), method.getName())));
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
                    WebUiBuiltInKeywords.class.getSimpleName(), method.getName(),
                    TestCaseEntityUtil.getKeywordJavaDocText(WebUiBuiltInKeywords.class.getName(), method.getName())));
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
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(keywordClass,
                keywordMethodName);
        if (keywordMethod == null) {
            return new HTMLActionParam[0];
        }

        List<HTMLActionParam> paramList = new ArrayList<HTMLActionParam>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter parameter = keywordMethod.getParameters()[i];
            if (parameter.isGeneralParam()) {
                paramList.add(new HTMLActionParam(parameter.getName(), parameter.getType()));
            }
        }
        return paramList.toArray(new HTMLActionParam[paramList.size()]);
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

    public static HTMLActionParamValueType[] generateParamDatas(IHTMLAction action,
            HTMLActionParamValueType[] existingParamDatas) {
        if (action == null || action.getParams() == null) {
            return new HTMLActionParamValueType[0];
        }

        HTMLActionParamValueType[] newParamDataArray = new HTMLActionParamValueType[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            HTMLActionParamValueType existingParamData = (existingParamDatas != null && i < existingParamDatas.length)
                    ? existingParamDatas[i] : null;

            if (!isAssignableFromScript(existingParamData, action.getParams()[i])) {
                InputValueEditorProvider valueType = AstInputValueTypeOptionsProvider
                        .getAssignableValueType(action.getParams()[i].getClazz());
                if (valueType != null) {
                    existingParamData = HTMLActionParamValueType.newInstance(valueType);
                }
            }
            newParamDataArray[i] = existingParamData;
        }
        return newParamDataArray;
    }

    private static boolean isAssignableFromScript(HTMLActionParamValueType existingParamData, HTMLActionParam param) {
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

    public static ExpressionWrapper convertToExpressionWrapper(String rawString) {
        return GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(rawString);
    }
}
