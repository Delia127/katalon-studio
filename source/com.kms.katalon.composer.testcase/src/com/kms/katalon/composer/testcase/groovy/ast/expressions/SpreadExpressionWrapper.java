package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.SpreadExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class SpreadExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;
    
    public SpreadExpressionWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
    }

    public SpreadExpressionWrapper(SpreadExpression spreadExpression, ASTNodeWrapper parentNodeWrapper) {
        super(spreadExpression, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(spreadExpression.getExpression(),
                this);
    }
    
    public SpreadExpressionWrapper(SpreadExpressionWrapper spreadExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(spreadExpressionWrapper, parentNodeWrapper);
        this.expression = spreadExpressionWrapper.getExpression().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    @Override
    public String getText() {
        return "*" + expression.getText();
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
    public SpreadExpressionWrapper clone() {
        return new SpreadExpressionWrapper(this, getParent());
    }
}
