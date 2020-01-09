package com.kms.katalon.composer.windows.record.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WindowsRecordedPayload {

    @SerializedName("Element")
    private WindowsRecordedElement element;

    @SerializedName("Parent")
    private List<WindowsRecordedElement> parent;

    @SerializedName("ActionName")
    private String actionName;

    @SerializedName("ActionData")
    private String actionData;

    public WindowsRecordedElement getElement() {
        return element;
    }

    public void setElement(WindowsRecordedElement element) {
        this.element = element;
    }

    public List<WindowsRecordedElement> getParent() {
        return parent;
    }

    public void setParent(List<WindowsRecordedElement> parent) {
        this.parent = parent;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }
}
