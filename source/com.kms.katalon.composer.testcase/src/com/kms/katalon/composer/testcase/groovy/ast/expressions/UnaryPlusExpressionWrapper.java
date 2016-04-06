package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.UnaryPlusExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class UnaryPlusExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;

    public UnaryPlusExpressionWrapper(UnaryPlusExpression unaryPlusExpression, ASTNodeWrapper parentNodeWrapper) {
        super(unaryPlusExpression, parentNodeWrapper);
        expression = ASTNodeWrapHelper
                .getExpressionNodeWrapperFromExpression(unaryPlusExpression.getExpression(), this);
    }
    
    public UnaryPlusExpressionWrapper(UnaryPlusExpressionWrapper unaryPlusExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(unaryPlusExpressionWrapper, parentNodeWrapper);
        expression = unaryPlusExpressionWrapper.getExpression().copy(this);
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
    public UnaryPlusExpressionWrapper clone() {
        return new UnaryPlusExpressionWrapper(this, getParent());
    }
}
