package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.PostfixExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;

public class PostfixExpressionWrapper extends ExpressionWrapper {
    private TokenWrapper operation;

    private ExpressionWrapper expression;

    public PostfixExpressionWrapper(ExpressionWrapper expression, TokenWrapper operation,
            ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.operation = operation;
        this.expression = expression;
    }

    public PostfixExpressionWrapper(PostfixExpression postfixExpression, ASTNodeWrapper parentNodeWrapper) {
        super(postfixExpression, parentNodeWrapper);
        this.operation = new TokenWrapper(postfixExpression.getOperation(), this);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(postfixExpression.getExpression(),
                this);
    }

    public PostfixExpressionWrapper(PostfixExpressionWrapper postfixExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(postfixExpressionWrapper, parentNodeWrapper);
        this.operation = new TokenWrapper(postfixExpressionWrapper.getOperation(), this);
        this.expression = postfixExpressionWrapper.getExpression().copy(this);
    }

    public TokenWrapper getOperation() {
        return operation;
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    @Override
    public String getText() {
        return "(" + expression.getText() + operation.getText() + ")";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.add(operation);
        return astNodeWrappers;
    }

    @Override
    public PostfixExpressionWrapper clone() {
        return new PostfixExpressionWrapper(this, getParent());
    }
}
