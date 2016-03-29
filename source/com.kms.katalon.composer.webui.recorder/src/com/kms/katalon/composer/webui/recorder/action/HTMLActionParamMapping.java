package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.composer.webui.recorder.action.IHTMLAction.HTMLActionParam;

public class HTMLActionParamMapping {
    private HTMLActionParamValueType actionData;
    private HTMLActionParam actionParam;

    public HTMLActionParamMapping(HTMLActionParam actionParam, HTMLActionParamValueType actionData) {
        this.setActionParam(actionParam);
        this.setActionData(actionData);
    }

    public HTMLActionParamValueType getActionData() {
        return actionData;
    }

    public void setActionData(HTMLActionParamValueType actionData) {
        this.actionData = actionData;
    }

    public HTMLActionParam getActionParam() {
        return actionParam;
    }

    public void setActionParam(HTMLActionParam actionParam) {
        this.actionParam = actionParam;
    }
}