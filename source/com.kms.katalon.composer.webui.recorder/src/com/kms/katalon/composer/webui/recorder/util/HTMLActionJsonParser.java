package com.kms.katalon.composer.webui.recorder.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class HTMLActionJsonParser {
    public static class HTMLActionJson {
        public static final String ACTION_DATA_NEW_VALUE_KEY = "newValue";

        public static final String ACTION_DATA_OLD_VALUE_KEY = "oldValue";

        public static final String DESELECT_ACTION_KEY = "deselect";

        public static final String SELECT_ACTION_KEY = "select";

        public static final String ACTION_WINDOW_ID_KEY = "windowId";

        public static final String SWITCH_TO_WINDOW_ACTION_KEY = "switchToWindow";

        public static final String SUBMIT_ACTION_KEY = "submit";

        public static final String DOUBLE_CLICK_ACTION_KEY = "doubleClick";

        public static final String MOUSE_CLICK_ACTION_DATA_RIGHT_CLICK = "right";

        public static final String MOUSE_CLICK_ACTION_DATA_LEFT_CLICK = "left";

        public static final String MOUSE_CLICK_ACTION_KEY = "click";

        public static final String ELEMENT_TYPE_TEXTAREA = "textarea";

        public static final String ELEMENT_TYPE_SELECT = "select";

        public static final String ELEMENT_TYPE_INPUT_CHECKBOX = "checkbox";

        public static final String ELEMENT_TYPE_INPUT = "input";

        public static final String INPUT_CHANGE_ACTION_KEY = "inputChange";

        public static final String NAVIGATE_ACTION_KEY = "navigate";

        public static final String ACTION_DATA_KEY = "actionData";

        public static final String ACTION_NAME_KEY = "actionName";

        public static final String ACTION_KEY = "action";

        private JsonObject actionObject;
        private String actionName;
        private String actionData;
        private HTMLElement element;

        public HTMLActionJson(JsonObject jsonObject) throws UnsupportedEncodingException {
            init(jsonObject);
        }

        private void init(JsonObject jsonObject) throws UnsupportedEncodingException {
            actionObject = getActionObject(jsonObject);
            if (actionObject == null) {
                throw new IllegalArgumentException();
            }
            actionName = getActionName(actionObject);
            if (actionName == null) {
                throw new IllegalArgumentException();
            }
            actionData = getActionData(actionObject);
            if (!actionName.equals(NAVIGATE_ACTION_KEY)) {
                element = HTMLElementUtil.buildHTMLElement(jsonObject, false);
            }
            if (isSelectOrDeselectAction(actionObject, actionName, element)) {
                JsonObject actionDataObject = actionObject.get(ACTION_DATA_KEY).getAsJsonObject();
                List<String> oldValues = getDataValueList(actionDataObject, ACTION_DATA_OLD_VALUE_KEY);
                List<String> newValues = getDataValueList(actionDataObject, ACTION_DATA_NEW_VALUE_KEY);
                if (newValues.size() > oldValues.size()) {
                    newValues.removeAll(oldValues);
                    actionName = SELECT_ACTION_KEY;
                    actionData = newValues.get(0);
                } else if (newValues.size() < oldValues.size()) {
                    oldValues.removeAll(newValues);
                    actionName = DESELECT_ACTION_KEY;
                    actionData = oldValues.get(0);
                } else {
                    actionName = SELECT_ACTION_KEY;
                    actionData = newValues.get(0);
                }
            }
        }

        private static boolean isSelectOrDeselectAction(JsonObject actionObject, String actionName, HTMLElement element) {
            return element != null && element.getType().toLowerCase().equals(ELEMENT_TYPE_SELECT)
                    && actionName.equals(INPUT_CHANGE_ACTION_KEY) && actionObject.get(ACTION_DATA_KEY).isJsonObject();
        }

        private static String getActionData(JsonObject actionObject) {
            if (actionObject.has(ACTION_DATA_KEY) && actionObject.get(ACTION_DATA_KEY).isJsonPrimitive()) {
                return actionObject.get(ACTION_DATA_KEY).getAsString();
            }
            return null;
        }

        private static JsonObject getActionObject(JsonObject jsonObject) {
            if (jsonObject.has(ACTION_KEY) && jsonObject.get(ACTION_KEY).isJsonObject()) {
                return jsonObject.getAsJsonObject(ACTION_KEY);
            }
            return null;
        }

        private static String getActionName(JsonObject actionObject) {
            if (actionObject.has(ACTION_NAME_KEY) && actionObject.get(ACTION_NAME_KEY).isJsonPrimitive()) {
                return actionObject.get(ACTION_NAME_KEY).getAsString();
            }
            return null;
        }

        private static List<String> getDataValueList(JsonObject actionDataObject, String memberKey) {
            List<String> dataValuesList = new ArrayList<String>();
            if (!actionDataObject.has(memberKey) || !actionDataObject.get(memberKey).isJsonArray()) {
                return dataValuesList;
            }
            JsonArray valueArray = actionDataObject.get(memberKey).getAsJsonArray();
            for (int i = 0; i < valueArray.size(); i++) {
                dataValuesList.add(valueArray.get(i).getAsString());
            }
            return dataValuesList;
        }

        public HTMLActionMapping buildActionMapping() {
            HTMLActionMapping newActionMapping = buildActionMapping(actionName, actionData, element);
            if (actionObject.has(ACTION_WINDOW_ID_KEY)) {
                newActionMapping.setWindowId(actionObject.get(ACTION_WINDOW_ID_KEY).getAsString());
            }
            return newActionMapping;
        }

        private static HTMLActionMapping buildActionMapping(String recordedActionName, String actionData,
                HTMLElement targetElement) {
            switch (recordedActionName) {
            case NAVIGATE_ACTION_KEY:
                return new HTMLActionMapping(HTMLAction.Navigate, actionData, targetElement);
            case INPUT_CHANGE_ACTION_KEY:
                switch (targetElement.getType().toLowerCase()) {
                case ELEMENT_TYPE_INPUT:
                    if (targetElement.getTypeAttribute() == null) {
                        return new HTMLActionMapping(HTMLAction.SetText, actionData, targetElement);
                    }
                    switch (targetElement.getTypeAttribute().toLowerCase()) {
                    case ELEMENT_TYPE_INPUT_CHECKBOX:
                        return new HTMLActionMapping(isActionDataTrue(actionData) ? HTMLAction.Check
                                : HTMLAction.Uncheck, actionData, targetElement);
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

        private static boolean isActionDataTrue(String actionData) {
            return actionData != null && actionData.toLowerCase().equals(Boolean.TRUE.toString().toLowerCase());
        }
    }

    public static HTMLActionMapping parseJsonIntoHTMLActionMapping(String jsonString) throws JsonSyntaxException,
            UnsupportedEncodingException {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(HTMLElementUtil.decodeURIComponent(jsonString));
        if (!(jsonElement instanceof JsonObject)) {
            return null;
        }
        return new HTMLActionJson((JsonObject) jsonElement).buildActionMapping();
    }
}
