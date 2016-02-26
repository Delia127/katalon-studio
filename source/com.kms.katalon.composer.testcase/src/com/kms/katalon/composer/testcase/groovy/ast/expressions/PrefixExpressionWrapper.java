package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.PrefixExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;

public class PrefixExpressionWrapper extends ExpressionWrapper {
    private TokenWrapper operation;
    private ExpressionWrapper expression;

    public PrefixExpressionWrapper(TokenWrapper operation, ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.operation = operation;
        this.expression = expression;
    }

    public PrefixExpressionWrapper(PrefixExpression prefixExpression, ASTNodeWrapper parentNodeWrapper) {
        super(prefixExpression, parentNodeWrapper);
        this.operation = new TokenWrapper(prefixExpression.getOperation(), this);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                prefixExpression.getExpression(), this);
    }
    
    public PrefixExpressionWrapper(PrefixExpressionWrapper prefixExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(prefixExpressionWrapper, parentNodeWrapper);
        this.operation = new TokenWrapper(prefixExpressionWrapper.getOperation(), this);
        this.expression = prefixExpressionWrapper.copy(this);
    }

    public TokenWrapper getOperation() {
        return operation;
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    @Override
    public String getText() {
        return "(" + operation.getText() + expression.getText() + ")";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(operation);
        astNodeWrappers.add(expression);
        return astNodeWrappers;
    }

    @Override
    public PrefixExpressionWrapper clone() {
        return new PrefixExpressionWrapper(this, getParent());
    }
}
