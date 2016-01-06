package com.kms.katalon.composer.webui.recorder.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class HTMLActionUtil {
    private static final String ACTION_DATA_NEW_VALUE_KEY = "newValue";

    private static final String ACTION_DATE_OLD_VALUE_KEY = "oldValue";

    private static final String DESELECT_ACTION_KEY = "deselect";

    private static final String SELECT_ACTION_KEY = "select";

    private static final String ACTION_WINDOW_ID_KEY = "windowId";

    private static final String SWITCH_TO_WINDOW_KEYWORD_NAME = "switchToWindowTitle";

    private static final String SWITCH_TO_WINDOW_ACTION_KEY = "switchToWindow";

    private static final String SUBMIT_KEYWORD_NAME = "submit";

    private static final String SUBMIT_ACTION_KEY = "submit";

    private static final String DOUBLE_CLICK_KEYWORD_NAME = "doubleClick";

    private static final String DOUBLE_CLICK_ACTION_KEY = "doubleClick";

    private static final String RIGHT_CLICK_KEYWORD_NAME = "rightClick";

    private static final String LEFT_CLICK_KEYWORD_NAME = "click";

    private static final String MOUSE_CLICK_ACTION_DATA_RIGHT_CLICK = "right";

    private static final String MOUSE_CLICK_ACTION_DATA_LEFT_CLICK = "left";

    private static final String MOUSE_CLICK_ACTION_KEY = "click";

    private static final String ELEMENT_TYPE_TEXTAREA = "textarea";

    private static final String DESELECT_OPTION_BY_VALUE_KEYWORD_NAME = "deselectOptionByValue";

    private static final String SELECT_OPTION_BY_VALUE_KEYWORD_NAME = "selectOptionByValue";

    private static final String ELEMENT_TYPE_SELECT = "select";

    private static final String SET_TEXT_KEYWORD_NAME = "setText";

    private static final String UNCHECK_KEYWORD_NAME = "uncheck";

    private static final String CHECK_KEYWORD_NAME = "check";

    private static final String ELEMENT_TYPE_INPUT_CHECKBOX = "checkbox";

    private static final String ELEMENT_TYPE_INPUT = "input";

    private static final String INPUT_CHANGE_ACTION_KEY = "inputChange";

    private static final String NAVIGATE_KEYWORD_NAME = "navigateToUrl";

    private static final String NAVIGATE_ACTION_KEY = "navigate";

    private static final String ACTION_DATA_KEY = "actionData";

    private static final String ACTION_NAME_KEY = "actionName";

    private static final String ACTION_KEY = "action";

    public static HTMLAction buildHTMLAction(String jsonString) throws Exception {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(HTMLElementUtil.decodeURIComponent(jsonString));
        String actionName = null;
        String actionData = null;
        HTMLElement element = null;
        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;

            JsonObject actionObject = jsonObject.getAsJsonObject(ACTION_KEY);
            if (actionObject != null) {
                actionName = actionObject.get(ACTION_NAME_KEY).getAsString();
            }
            if (actionName != null) {
                if (!actionName.equals(NAVIGATE_ACTION_KEY)) {
                    element = HTMLElementUtil.buildHTMLElement(jsonObject, false);

                    if (element.getType().toLowerCase().equals(ELEMENT_TYPE_SELECT)
                            && actionName.equals(INPUT_CHANGE_ACTION_KEY)) {
                        JsonObject actionDataObject = actionObject.get(ACTION_DATA_KEY).getAsJsonObject();
                        List<String> oldArray = new ArrayList<String>();
                        List<String> newArray = new ArrayList<String>();
                        JsonArray valueArray = actionDataObject.get(ACTION_DATE_OLD_VALUE_KEY).getAsJsonArray();
                        for (int i = 0; i < valueArray.size(); i++) {
                            oldArray.add(valueArray.get(i).getAsString());
                        }
                        valueArray = actionDataObject.get(ACTION_DATA_NEW_VALUE_KEY).getAsJsonArray();
                        for (int i = 0; i < valueArray.size(); i++) {
                            newArray.add(valueArray.get(i).getAsString());
                        }
                        if (newArray.size() > oldArray.size()) {
                            newArray.removeAll(oldArray);
                            actionName = SELECT_ACTION_KEY;
                            actionData = newArray.get(0);
                        } else if (newArray.size() < oldArray.size()) {
                            oldArray.removeAll(newArray);
                            actionName = DESELECT_ACTION_KEY;
                            actionData = oldArray.get(0);
                        } else {
                            actionName = SELECT_ACTION_KEY;
                            actionData = newArray.get(0);
                        }
                    } else {
                        actionData = actionObject.get(ACTION_DATA_KEY).getAsString();
                    }
                } else {
                    actionData = actionObject.get(ACTION_DATA_KEY).getAsString();
                }

                HTMLAction action = new HTMLAction(actionName, element, actionData);
                if (actionObject.get(ACTION_WINDOW_ID_KEY) != null) {
                    action.setWindowId(actionObject.get(ACTION_WINDOW_ID_KEY).getAsString());
                }
                return action;
            }

        }
        return null;
    }

    public static Statement generateWebUiTestStep(HTMLAction action, WebElementEntity createdTestObject)
            throws Exception {
        String methodName = generateWebUiMethodNameFromHTMLAction(action.getActionName(), action.getActionData(),
                action.getTargetElement());
        Method method = null;
        for (Method declareMethod : WebUiBuiltInKeywords.class.getDeclaredMethods()) {
            if (declareMethod.getName().equals(methodName)) {
                method = declareMethod;
                break;
            }
        }
        if (method != null) {
            List<Expression> arguments = new ArrayList<Expression>();
            for (Type parameterType : method.getGenericParameterTypes()) {
                if (parameterType instanceof Class<?>) {
                    Class<?> clazz = (Class<?>) parameterType;
                    Object data = null;
                    if (clazz.getName().equals(TestObject.class.getName())) {
                        data = createdTestObject;
                    } else {
                        data = action.getActionData();
                    }
                    Expression generatedExression = generateArgumentForWebUiMethod(clazz, data);
                    if (generatedExression != null) {
                        arguments.add(generateArgumentForWebUiMethod(clazz, data));
                    }
                }
            }
            ArgumentListExpression argumentListExpression = new ArgumentListExpression(arguments);
            MethodCallExpression methodCallExpression = new MethodCallExpression(new VariableExpression(
                    WebUiBuiltInKeywords.class.getSimpleName()), methodName, argumentListExpression);
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

    private static Expression generateArgumentForWebUiMethod(Class<?> argumentClass, Object data) throws Exception {
        if (argumentClass.getName().equals(TestObject.class.getName()) && data instanceof WebElementEntity) {
            return generateObjectMethodCall(((WebElementEntity) data).getIdForDisplay());
        } else if (argumentClass.getName().equals(FailureHandling.class.getName())) {
            return generateFailureHandlingExpression();
        } else if (data != null && argumentClass.getName().equals(data.getClass().getName())) {
            return new ConstantExpression(data);
        } else {
            if (argumentClass.getName().equals(String.class.getName())) {
                return new ConstantExpression("");
            } else if (argumentClass.getName().equals(Boolean.class.getName())
                    || argumentClass.getName().equals(Boolean.TYPE.getName())) {
                return new ConstantExpression(false);
            }
            return new ConstantExpression(null);
        }
    }

    public static Expression generateFailureHandlingExpression() {
        return AstTreeTableInputUtil.createPropertyExpressionForClass(FailureHandling.class.getSimpleName(),
                TestCasePreferenceDefaultValueInitializer.getDefaultFailureHandling().name());
    }

    private static String generateWebUiMethodNameFromHTMLAction(String action, String actionData,
            HTMLElement htmlElement) {
        switch (action) {
            case NAVIGATE_ACTION_KEY:
                return NAVIGATE_KEYWORD_NAME;
            case INPUT_CHANGE_ACTION_KEY:
                switch (htmlElement.getType().toLowerCase()) {
                    case ELEMENT_TYPE_INPUT:
                        if (htmlElement.getTypeAttribute() != null) {
                            switch (htmlElement.getTypeAttribute().toLowerCase()) {
                                case ELEMENT_TYPE_INPUT_CHECKBOX:
                                    if (actionData.toLowerCase().equals(Boolean.TRUE.toString().toLowerCase())) {
                                        return CHECK_KEYWORD_NAME;
                                    } else if (actionData.toLowerCase().equals(Boolean.FALSE.toString().toLowerCase())) {
                                        return UNCHECK_KEYWORD_NAME;
                                    }
                            }
                        }
                        return SET_TEXT_KEYWORD_NAME;
                    case ELEMENT_TYPE_TEXTAREA:
                        return SET_TEXT_KEYWORD_NAME;
                }
            case SELECT_ACTION_KEY:
                return SELECT_OPTION_BY_VALUE_KEYWORD_NAME;
            case DESELECT_ACTION_KEY:
                return DESELECT_OPTION_BY_VALUE_KEYWORD_NAME;
            case MOUSE_CLICK_ACTION_KEY:
                switch (actionData) {
                    case MOUSE_CLICK_ACTION_DATA_LEFT_CLICK:
                        return LEFT_CLICK_KEYWORD_NAME;
                    case MOUSE_CLICK_ACTION_DATA_RIGHT_CLICK:
                        return RIGHT_CLICK_KEYWORD_NAME;
                }
            case DOUBLE_CLICK_ACTION_KEY:
                return DOUBLE_CLICK_KEYWORD_NAME;
            case SUBMIT_ACTION_KEY:
                return SUBMIT_KEYWORD_NAME;
            case SWITCH_TO_WINDOW_ACTION_KEY:
                return SWITCH_TO_WINDOW_KEYWORD_NAME;
        }
        return StringUtils.EMPTY;
    }

    public static boolean verifyAction(HTMLAction action, List<HTMLAction> existingActions) {
        if (action.getActionName().equals(NAVIGATE_ACTION_KEY) && existingActions.size() > 0) {
            // for (int i = existingActions.size() - 1; i >= 0; i--) {
            // HTMLAction previousAction = existingActions.get(i);
            // if
            // ((previousAction.getActionName().equals(SWITCH_TO_WINDOW_ACTION_KEY)
            // || previousAction
            // .getActionName().equals(NAVIGATE_ACTION_KEY))
            // &&
            // !previousAction.getActionData().equals(action.getActionData())) {
            // action.setActionName(SWITCH_TO_WINDOW_ACTION_KEY);
            // return true;
            // }
            // }
            // return false;
            return false;
        }
        if (action.getActionName().equals(DOUBLE_CLICK_ACTION_KEY) && existingActions.size() >= 2) {
            HTMLAction actionOffset_1 = existingActions.get(existingActions.size() - 1);
            HTMLAction actionOffset_2 = existingActions.get(existingActions.size() - 2);
            if (actionOffset_1.getActionName().equals(MOUSE_CLICK_ACTION_KEY)
                    && actionOffset_2.getActionName().equals(MOUSE_CLICK_ACTION_KEY)
                    && actionOffset_1.getTargetElement().equals(action.getTargetElement())
                    && actionOffset_2.getTargetElement().equals(action.getTargetElement())) {
                existingActions.remove(actionOffset_1);
                existingActions.remove(actionOffset_2);
            }
        }
        return true;
    }

    public static HTMLAction createNewSwitchToWindowAction(String data) {
        return new HTMLAction(SWITCH_TO_WINDOW_ACTION_KEY, null, data);

    }

    public static String getPageTitleForAction(HTMLAction action) {
        HTMLElement element = action.getTargetElement();
        while (!(element == null) && !(element instanceof HTMLPageElement)) {
            element = element.getParentElement();
        }
        if (element != null) {
            return ((HTMLPageElement) element).getAttributes().get(HTMLElementUtil.PAGE_TITLE_KEY);
        }
        return null;
    }
}
