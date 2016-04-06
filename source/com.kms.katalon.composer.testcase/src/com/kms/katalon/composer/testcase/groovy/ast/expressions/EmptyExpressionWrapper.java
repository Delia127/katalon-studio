package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import org.codehaus.groovy.ast.expr.EmptyExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class EmptyExpressionWrapper extends ExpressionWrapper {

    public EmptyExpressionWrapper(EmptyExpression emptyExpression, ASTNodeWrapper parentNodeWrapper) {
        super(emptyExpression, parentNodeWrapper);
    }
    
    public EmptyExpressionWrapper(EmptyExpressionWrapper emptyExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(emptyExpressionWrapper, parentNodeWrapper);
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public EmptyExpressionWrapper clone() {
        return new EmptyExpressionWrapper(this, getParent());
    }
}
