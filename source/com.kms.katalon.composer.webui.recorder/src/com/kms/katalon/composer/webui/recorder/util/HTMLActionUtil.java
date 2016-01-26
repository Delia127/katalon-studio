package com.kms.katalon.composer.webui.recorder.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLSynchronizeAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLValidationAction;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction;
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

    private static final String SWITCH_TO_WINDOW_ACTION_KEY = "switchToWindow";

    private static final String SUBMIT_ACTION_KEY = "submit";

    private static final String DOUBLE_CLICK_ACTION_KEY = "doubleClick";

    private static final String MOUSE_CLICK_ACTION_DATA_RIGHT_CLICK = "right";

    private static final String MOUSE_CLICK_ACTION_DATA_LEFT_CLICK = "left";

    private static final String MOUSE_CLICK_ACTION_KEY = "click";

    private static final String ELEMENT_TYPE_TEXTAREA = "textarea";

    private static final String ELEMENT_TYPE_SELECT = "select";

    private static final String ELEMENT_TYPE_INPUT_CHECKBOX = "checkbox";

    private static final String ELEMENT_TYPE_INPUT = "input";

    private static final String INPUT_CHANGE_ACTION_KEY = "inputChange";

    private static final String NAVIGATE_ACTION_KEY = "navigate";

    private static final String ACTION_DATA_KEY = "actionData";

    private static final String ACTION_NAME_KEY = "actionName";

    private static final String ACTION_KEY = "action";

    public static HTMLActionMapping buildActionMapping(String jsonString) throws Exception {
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

                HTMLActionMapping newActionMapping = buildActionMapping(actionName, actionData, element);
                if (actionObject.get(ACTION_WINDOW_ID_KEY) != null) {
                    newActionMapping.setWindowId(actionObject.get(ACTION_WINDOW_ID_KEY).getAsString());
                }
                return newActionMapping;
            }

        }
        return null;
    }

    public static HTMLActionMapping buildActionMapping(String recordedActionName, String actionData,
            HTMLElement targetElement) {
        switch (recordedActionName) {
        case NAVIGATE_ACTION_KEY:
            return new HTMLActionMapping(HTMLAction.Navigate, actionData, targetElement);
        case INPUT_CHANGE_ACTION_KEY:
            switch (targetElement.getType().toLowerCase()) {
            case ELEMENT_TYPE_INPUT:
                if (targetElement.getTypeAttribute() != null) {
                    switch (targetElement.getTypeAttribute().toLowerCase()) {
                    case ELEMENT_TYPE_INPUT_CHECKBOX:
                        if (actionData.toLowerCase().equals(Boolean.TRUE.toString().toLowerCase())) {
                            return new HTMLActionMapping(HTMLAction.Check, actionData, targetElement);
                        } else if (actionData.toLowerCase().equals(Boolean.FALSE.toString().toLowerCase())) {
                            return new HTMLActionMapping(HTMLAction.Uncheck, actionData, targetElement);
                        }
                    }
                }
                return new HTMLActionMapping(HTMLAction.SetText, actionData, targetElement);
            case ELEMENT_TYPE_TEXTAREA:
                return new HTMLActionMapping(HTMLAction.SetText, actionData, targetElement);
            }
        case SELECT_ACTION_KEY:
            return new HTMLActionMapping(HTMLAction.Select, actionData, targetElement);
        case DESELECT_ACTION_KEY:
            return new HTMLActionMapping(HTMLAction.Deselect, actionData, targetElement);
        case MOUSE_CLICK_ACTION_KEY:
            switch (actionData) {
            case MOUSE_CLICK_ACTION_DATA_LEFT_CLICK:
                return new HTMLActionMapping(HTMLAction.LeftClick, "", targetElement);
            case MOUSE_CLICK_ACTION_DATA_RIGHT_CLICK:
                return new HTMLActionMapping(HTMLAction.RightClick, "", targetElement);
            }
        case DOUBLE_CLICK_ACTION_KEY:
            return new HTMLActionMapping(HTMLAction.DoubleClick, actionData, targetElement);
        case SUBMIT_ACTION_KEY:
            return new HTMLActionMapping(HTMLAction.Submit, actionData, targetElement);
        case SWITCH_TO_WINDOW_ACTION_KEY:
            return new HTMLActionMapping(HTMLAction.SwitchToWindow, actionData, targetElement);
        }
        return null;
    }

    public static Statement generateWebUiTestStep(HTMLActionMapping actionMapping, WebElementEntity createdTestObject)
            throws Exception {
        String methodName = actionMapping.getAction().getMappedKeywordMethod();
        // generateWebUiMethodNameFromHTMLAction(action.getActionName(), action.getActionData(),
        // action.getTargetElement());
        Class<?> keywordClass = Class.forName(actionMapping.getAction().getMappedKeywordClass());
        Method method = null;
        for (Method declareMethod : keywordClass.getDeclaredMethods()) {
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
                        data = actionMapping.getData();
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

    public static boolean verifyActionMapping(HTMLActionMapping actionMapping,
            List<HTMLActionMapping> existingActionMappings) {
        if (actionMapping == null || actionMapping.getAction() == null) {
            return false;
        }
        if (actionMapping.getAction() == HTMLAction.Navigate && existingActionMappings.size() > 0) {
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
        if (actionMapping.getAction().getName().equals(DOUBLE_CLICK_ACTION_KEY) && existingActionMappings.size() >= 2) {
            HTMLActionMapping actionOffset_1 = existingActionMappings.get(existingActionMappings.size() - 1);
            HTMLActionMapping actionOffset_2 = existingActionMappings.get(existingActionMappings.size() - 2);
            if (actionOffset_1.getAction().getName().equals(MOUSE_CLICK_ACTION_KEY)
                    && actionOffset_2.getAction().getName().equals(MOUSE_CLICK_ACTION_KEY)
                    && actionOffset_1.getTargetElement().equals(actionMapping.getTargetElement())
                    && actionOffset_2.getTargetElement().equals(actionMapping.getTargetElement())) {
                existingActionMappings.remove(actionOffset_1);
                existingActionMappings.remove(actionOffset_2);
            }
        }
        return true;
    }

    public static HTMLActionMapping createNewSwitchToWindowAction(String data) {
        return new HTMLActionMapping(HTMLAction.SwitchToWindow, data, null);

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
        List<HTMLValidationAction> result = new ArrayList<HTMLValidationAction>();
        for (Method method : WebUiBuiltInKeywords.class.getDeclaredMethods()) {
            if (method.getName().startsWith(HTMLValidationAction.VALIDATION_ACTION_PREFIX)) {
                result.add(new HTMLValidationAction(method.getName(), WebUiBuiltInKeywords.class.getName(), method
                        .getName()));
            }
        }
        return result;
    }

    public static List<HTMLSynchronizeAction> getAllHTMLSynchronizeActions() {
        List<HTMLSynchronizeAction> result = new ArrayList<HTMLSynchronizeAction>();
        for (Method method : WebUiBuiltInKeywords.class.getDeclaredMethods()) {
            if (method.getName().startsWith(HTMLSynchronizeAction.SYNCHRONIZE_ACTION_PREFIX)) {
                result.add(new HTMLSynchronizeAction(method.getName(), WebUiBuiltInKeywords.class.getName(), method
                        .getName()));
            }
        }
        return result;
    }
}
