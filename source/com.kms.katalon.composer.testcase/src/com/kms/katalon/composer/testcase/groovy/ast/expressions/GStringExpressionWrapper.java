package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import org.codehaus.groovy.ast.expr.GStringExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class GStringExpressionWrapper extends ExpressionWrapper {
    private String verbatimText;
    
    public GStringExpressionWrapper(GStringExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        this.verbatimText = expression.getText();
    }

    public GStringExpressionWrapper(String verbatimText, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.verbatimText = verbatimText;
    }
    
    public GStringExpressionWrapper(GStringExpressionWrapper gStringExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(gStringExpressionWrapper, parentNodeWrapper);
        this.verbatimText = gStringExpressionWrapper.getText();
    }

    @Override
    public String getText() {
        return getVerbatimText();
    }
    
    public String getVerbatimText() {
        return verbatimText;
    }
    
    @Override
    public GStringExpressionWrapper clone() {
        return new GStringExpressionWrapper(this, getParent());
    }
}
