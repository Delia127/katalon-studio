package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.objectspy.element.HTMLElement;

public class HTMLActionMapping {
    private IHTMLAction action;
    private Object[] paramDatas;
    private HTMLElement targetElement;
    private String windowId;

    public HTMLActionMapping(IHTMLAction action, String recordedData, HTMLElement targetElement) {
        paramDatas = new Object[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            if (action.getParams()[i].getClazz().isAssignableFrom(String.class)) {
                paramDatas[i] = recordedData;
            } else {
                paramDatas[i] = null;
            }
        }
        this.setAction(action);
        this.setTargetElement(targetElement);
    }

    public HTMLActionMapping(IHTMLAction action, Object[] data, HTMLElement targetElement) {
        this.setTargetElement(targetElement);
        this.setData(data);
        this.setAction(action);
    }

    public HTMLActionMapping(IHTMLAction action, HTMLElement targetElement) {
        this.setAction(action);
        this.setTargetElement(targetElement);
    }

    public Object[] getData() {
        return paramDatas;
    }

    public void setData(Object[] paramDatas) {
        this.paramDatas = paramDatas;
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
        paramDatas = HTMLActionUtil.generateParamDatas(action, paramDatas);
        if (!action.hasElement()) {
            targetElement = null;
        }
    }

    public String getWindowId() {
        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }
}
