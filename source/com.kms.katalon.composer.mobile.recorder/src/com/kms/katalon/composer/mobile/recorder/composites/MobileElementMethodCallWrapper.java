package com.kms.katalon.composer.mobile.recorder.composites;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;

public class MobileElementMethodCallWrapper extends MethodCallExpressionWrapper {

    private CapturedMobileElement mobileElement;

    public MobileElementMethodCallWrapper(ASTNodeWrapper parentNodeWrapper, CapturedMobileElement webElement) {
        super(THIS_VARIABLE, MethodCallExpressionWrapper.FIND_WINDOWS_OBJECT_METHOD_NAME, parentNodeWrapper);
        this.mobileElement = webElement;
    }

    @Override
    public String getText() {
        return mobileElement.getName();
    }

    public CapturedMobileElement getWindowsElement() {
        return mobileElement;
    }

    @Override
    public ArgumentListExpressionWrapper getArguments() {
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(this);
        argument.addExpression(new ConstantExpressionWrapper(mobileElement.getScriptId(), argument));

        return argument;
    }
}
