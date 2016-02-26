package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.UnaryMinusExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class UnaryMinusExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;

    public UnaryMinusExpressionWrapper(UnaryMinusExpression unaryMinusExpression, ASTNodeWrapper parentNodeWrapper) {
        super(unaryMinusExpression, parentNodeWrapper);
        expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(unaryMinusExpression.getExpression(),
                this);
    }
    
    public UnaryMinusExpressionWrapper(UnaryMinusExpressionWrapper unaryMinusExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(unaryMinusExpressionWrapper, parentNodeWrapper);
        expression = unaryMinusExpressionWrapper.getExpression().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    @Override
    public String getText() {
        return expression.getText();
    }
    
    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        return astNodeWrappers;
    }

    @Override
    public UnaryMinusExpressionWrapper clone() {
        return new UnaryMinusExpressionWrapper(this, getParent());
    }
}
