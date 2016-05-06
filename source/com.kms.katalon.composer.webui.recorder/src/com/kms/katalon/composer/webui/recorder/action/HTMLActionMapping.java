package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.objectspy.element.HTMLElement;

public class HTMLActionMapping {
    private IHTMLAction action;
    private HTMLActionParamValueType[] paramDatas;
    private HTMLElement targetElement;
    private String windowId;

    public HTMLActionMapping(IHTMLAction action, String recordedData, HTMLElement targetElement) {
        paramDatas = new HTMLActionParamValueType[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            if (action.getParams()[i].getClazz().isAssignableFrom(String.class)) {
                paramDatas[i] = HTMLActionParamValueType.newInstance(InputValueType.String,
                        HTMLActionUtil.convertToExpressionWrapper(GroovyStringUtil.toGroovyStringFormat(recordedData)));
            }
        }
        this.setAction(action);
        this.setTargetElement(targetElement);
    }

    public HTMLActionMapping(IHTMLAction action, HTMLActionParamValueType[] data, HTMLElement targetElement) {
        this.setTargetElement(targetElement);
        this.setData(data);
        this.setAction(action);
    }

    public HTMLActionMapping(IHTMLAction action, HTMLElement targetElement) {
        this.setAction(action);
        this.setTargetElement(targetElement);
    }

    public HTMLActionParamValueType[] getData() {
        return paramDatas;
    }

    public void setData(HTMLActionParamValueType[] paramDatas) {
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
