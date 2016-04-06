package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.SpreadMapExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class SpreadMapExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;
    
    public SpreadMapExpressionWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
    }

    public SpreadMapExpressionWrapper(SpreadMapExpression spreadMapExpression, ASTNodeWrapper parentNodeWrapper) {
        super(spreadMapExpression, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(spreadMapExpression.getExpression(),
                this);
    }
    
    public SpreadMapExpressionWrapper(SpreadMapExpressionWrapper spreadMapExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(spreadMapExpressionWrapper, parentNodeWrapper);
        this.expression = spreadMapExpressionWrapper.getExpression().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    @Override
    public String getText() {
        return "*:" + expression.getText();
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
    public SpreadMapExpressionWrapper clone() {
        return new SpreadMapExpressionWrapper(this, getParent());
    }
}
