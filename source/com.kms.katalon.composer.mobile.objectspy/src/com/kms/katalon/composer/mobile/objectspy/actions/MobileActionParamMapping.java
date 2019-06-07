package com.kms.katalon.composer.mobile.objectspy.actions;

public class MobileActionParamMapping {
    private MobileActionParamValueType actionData;

    private MobileActionParam actionParam;

    public MobileActionParamMapping(MobileActionParam actionParam, MobileActionParamValueType actionData) {
        this.setActionParam(actionParam);
        this.setActionData(actionData);
    }

    public MobileActionParamValueType getActionData() {
        return actionData;
    }

    public void setActionData(MobileActionParamValueType actionData) {
        this.actionData = actionData;
    }

    public MobileActionParam getActionParam() {
        return actionParam;
    }

    public void setActionParam(MobileActionParam actionParam) {
        this.actionParam = actionParam;
    }
}
