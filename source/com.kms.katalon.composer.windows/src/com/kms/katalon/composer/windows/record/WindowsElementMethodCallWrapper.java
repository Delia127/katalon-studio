package com.kms.katalon.composer.windows.record;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;

public class WindowsElementMethodCallWrapper extends MethodCallExpressionWrapper {

    private CapturedWindowsElement windowsElement;

    public WindowsElementMethodCallWrapper(ASTNodeWrapper parentNodeWrapper, CapturedWindowsElement webElement) {
        super(THIS_VARIABLE, MethodCallExpressionWrapper.FIND_WINDOWS_OBJECT_METHOD_NAME, parentNodeWrapper);
        this.windowsElement = webElement;
    }

    @Override
    public String getText() {
        return windowsElement.getName();
    }

    public CapturedWindowsElement getWindowsElement() {
        return windowsElement;
    }

    @Override
    public ArgumentListExpressionWrapper getArguments() {
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(this);
        argument.addExpression(new ConstantExpressionWrapper(windowsElement.getScriptId(), argument));

        return argument;
    }
}
