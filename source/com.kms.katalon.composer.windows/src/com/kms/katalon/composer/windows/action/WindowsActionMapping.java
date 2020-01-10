package com.kms.katalon.composer.windows.action;

import java.util.Date;

import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.groovy.util.GroovyStringUtil;

public class WindowsActionMapping {
    private IWindowsAction action;

    private WindowsActionParamValueType[] paramDatas;

    private CapturedWindowsElement targetElement;
    
    private Date recordedTime;

    public WindowsActionMapping(IWindowsAction action, String recordedData, CapturedWindowsElement targetElement) {
        paramDatas = new WindowsActionParamValueType[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            WindowsActionParam mobileActionParam = action.getParams()[i];
            if (mobileActionParam.getClazz().isAssignableFrom(String.class)) {
                paramDatas[i] = WindowsActionParamValueType.newInstance(InputValueType.String,
                        mobileActionParam.getName(), GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(
                                GroovyStringUtil.toGroovyStringFormat(recordedData)));
            }
        }
        this.setTargetElement(targetElement);
        this.setAction(action);
    }

    public WindowsActionMapping(IWindowsAction action, WindowsActionParamValueType[] data,
            CapturedWindowsElement targetElement) {
        this.setData(data);
        this.setTargetElement(targetElement);
        this.setAction(action);
    }

    public WindowsActionMapping(IWindowsAction action, CapturedWindowsElement targetElement) {
        this.setTargetElement(targetElement);
        this.setAction(action);
    }

    public WindowsActionParamValueType[] getData() {
        return paramDatas;
    }

    public void setData(WindowsActionParamValueType[] paramDatas) {
        this.paramDatas = paramDatas;
    }

    public CapturedWindowsElement getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(CapturedWindowsElement targetElement) {
        this.targetElement = targetElement;
    }

    public IWindowsAction getAction() {
        return action;
    }

    public void setAction(IWindowsAction action) {
        this.action = action;
        paramDatas = WindowsActionUtil.generateParamDatas(action, paramDatas);
        if (!action.hasElement()) {
            setTargetElement(null);
        }
    }

    public Date getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(Date recordedTime) {
        this.recordedTime = recordedTime;
    }
}
