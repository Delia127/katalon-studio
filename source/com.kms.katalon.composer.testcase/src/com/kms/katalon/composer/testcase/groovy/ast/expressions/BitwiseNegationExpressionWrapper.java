package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class BitwiseNegationExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;

    public BitwiseNegationExpressionWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
    }

    public BitwiseNegationExpressionWrapper(BitwiseNegationExpressionWrapper bitwiseNegationExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(bitwiseNegationExpressionWrapper, parentNodeWrapper);
        this.expression = bitwiseNegationExpressionWrapper.copy(this);
    }

    public BitwiseNegationExpressionWrapper(BitwiseNegationExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getExpression(), this);
    }

    @Override
    public String getText() {
        return expression.getText();
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        return astNodeWrappers;
    }

    @Override
    public BitwiseNegationExpressionWrapper clone() {
        return new BitwiseNegationExpressionWrapper(this, getParent());
    }
}
