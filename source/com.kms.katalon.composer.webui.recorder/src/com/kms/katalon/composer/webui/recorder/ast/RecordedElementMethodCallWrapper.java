package com.kms.katalon.composer.webui.recorder.ast;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.objectspy.element.WebElement;

public class RecordedElementMethodCallWrapper extends MethodCallExpressionWrapper {
    public RecordedElementMethodCallWrapper(ASTNodeWrapper parentNodeWrapper, WebElement webElement) {
        super(THIS_VARIABLE, MethodCallExpressionWrapper.FIND_TEST_OBJECT_METHOD_NAME, parentNodeWrapper);
        this.webElement = webElement;
    }

    private WebElement webElement;
   
    @Override
    public String getText() {
        return webElement.getName();
    }

    public WebElement getWebElement() {
        return webElement;
    }
    
    @Override
    public ArgumentListExpressionWrapper getArguments() {
        ArgumentListExpressionWrapper argument = new ArgumentListExpressionWrapper(this);
        argument.addExpression(
                new ConstantExpressionWrapper(webElement.getScriptId(), argument));
        
        return argument;
    }
}
