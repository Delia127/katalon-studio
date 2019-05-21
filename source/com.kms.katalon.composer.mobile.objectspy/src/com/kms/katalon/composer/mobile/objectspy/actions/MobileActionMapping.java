package com.kms.katalon.composer.mobile.objectspy.actions;

import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.util.MobileActionUtil;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.groovy.util.GroovyStringUtil;

public class MobileActionMapping {
    private IMobileAction action;

    private MobileActionParamValueType[] paramDatas;

    private MobileElement targetElement;

    public MobileActionMapping(IMobileAction action, String recordedData, MobileElement targetElement) {
        paramDatas = new MobileActionParamValueType[action.getParams().length];
        for (int i = 0; i < action.getParams().length; i++) {
            MobileActionParam mobileActionParam = action.getParams()[i];
            if (mobileActionParam.getClazz().isAssignableFrom(String.class)) {
                paramDatas[i] = MobileActionParamValueType.newInstance(InputValueType.String,
                        mobileActionParam.getName(), GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(
                                GroovyStringUtil.toGroovyStringFormat(recordedData)));
            }
        }
        this.setTargetElement(targetElement);
        this.setAction(action);
    }

    public MobileActionMapping(IMobileAction action, MobileActionParamValueType[] data, MobileElement targetElement) {
        this.setData(data);
        this.setTargetElement(targetElement);
        this.setAction(action);
    }

    public MobileActionMapping(IMobileAction action, MobileElement targetElement) {
        this.setTargetElement(targetElement);
        this.setAction(action);
    }

    public MobileActionParamValueType[] getData() {
        return paramDatas;
    }

    public void setData(MobileActionParamValueType[] paramDatas) {
        this.paramDatas = paramDatas;
    }

    public MobileElement getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(MobileElement targetElement) {
        this.targetElement = targetElement;
    }

    public IMobileAction getAction() {
        return action;
    }

    public void setAction(IMobileAction action) {
        this.action = action;
        paramDatas = MobileActionUtil.generateParamDatas(action, paramDatas);
        if (!action.hasElement()) {
            setTargetElement(null);
        }
    }
}
