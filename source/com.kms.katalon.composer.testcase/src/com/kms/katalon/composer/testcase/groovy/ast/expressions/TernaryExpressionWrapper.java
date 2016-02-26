package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.TernaryExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class TernaryExpressionWrapper extends ExpressionWrapper {
    private BooleanExpressionWrapper booleanExpression;
    private ExpressionWrapper trueExpression;
    private ExpressionWrapper falseExpression;

    public TernaryExpressionWrapper(BooleanExpressionWrapper booleanExpression, ExpressionWrapper trueExpression,
            ExpressionWrapper falseExpression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = booleanExpression;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    public TernaryExpressionWrapper(TernaryExpression ternaryExpression, ASTNodeWrapper parentNodeWrapper) {
        super(ternaryExpression, parentNodeWrapper);
        booleanExpression = new BooleanExpressionWrapper(ternaryExpression.getBooleanExpression(), this);
        trueExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                ternaryExpression.getTrueExpression(), this);
        falseExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                ternaryExpression.getFalseExpression(), this);
    }
    
    public TernaryExpressionWrapper(TernaryExpressionWrapper ternaryExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(ternaryExpressionWrapper, parentNodeWrapper);
        booleanExpression = new BooleanExpressionWrapper(ternaryExpressionWrapper.getBooleanExpression(), this);
        trueExpression = ternaryExpressionWrapper.getTrueExpression().copy(this);
        falseExpression = ternaryExpressionWrapper.getFalseExpression().copy(this);
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return booleanExpression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    public ExpressionWrapper getTrueExpression() {
        return trueExpression;
    }

    public void setTrueExpression(ExpressionWrapper trueExpression) {
        this.trueExpression = trueExpression;
    }

    public ExpressionWrapper getFalseExpression() {
        return falseExpression;
    }

    public void setFalseExpression(ExpressionWrapper falseExpression) {
        this.falseExpression = falseExpression;
    }

    @Override
    public String getText() {
        return "(" + booleanExpression.getText() + ") ? " + trueExpression.getText() + " : "
                + falseExpression.getText();
    }
    
    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(booleanExpression);
        astNodeWrappers.add(trueExpression);
        astNodeWrappers.add(falseExpression);
        return astNodeWrappers;
    }

    @Override
    public TernaryExpressionWrapper clone() {
        return new TernaryExpressionWrapper(this, getParent());
    }
}
