package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.objectspy.element.HTMLElement;

public class HTMLActionMapping {
    private IHTMLAction action;
    private Object data;
    private HTMLElement targetElement;
    private String windowId;
    
    public HTMLActionMapping(IHTMLAction action, String data, HTMLElement targetElement) {
        this.setAction(action);
        this.setData(data);
        this.setTargetElement(targetElement);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HTMLElement getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(HTMLElement targetElement) {
        this.targetElement = targetElement;
    }


    public IHTMLAction getAction() {
        return action;
    }

    public void setAction(IHTMLAction action) {
        this.action = action;
    }

    public String getWindowId() {
        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }
}
