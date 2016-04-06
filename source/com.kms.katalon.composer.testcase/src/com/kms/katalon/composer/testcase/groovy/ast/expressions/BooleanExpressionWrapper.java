package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.NotExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class BooleanExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;
    private boolean isReverse = false;

    public BooleanExpressionWrapper(BooleanExpressionWrapper booleanExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(booleanExpressionWrapper, parentNodeWrapper);
        copyProperties(booleanExpressionWrapper);
    }

    public BooleanExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = new ConstantExpressionWrapper(true, this);
        this.isReverse = false;
    }

    public BooleanExpressionWrapper(BooleanExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        if (expression.getExpression() instanceof NotExpression) {
            this.isReverse = true;
            this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                    ((NotExpression) expression.getExpression()).getExpression(), this);
            return;
        }
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getExpression(), this);
    }

    private void copyProperties(BooleanExpressionWrapper booleanExpressionWrapper) {
        this.expression = booleanExpressionWrapper.getExpression().copy(this);
        this.isReverse = booleanExpressionWrapper.isReverse();
    }

    @Override
    public String getText() {
        if (isReverse) {
            return "!(" + expression.getText() + ")";
        }
        return expression.getText();
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        this.expression = expression;
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

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        this.isReverse = reverse;
    }

    @Override
    public BooleanExpressionWrapper clone() {
        return new BooleanExpressionWrapper(this, getParent());
    }
}
