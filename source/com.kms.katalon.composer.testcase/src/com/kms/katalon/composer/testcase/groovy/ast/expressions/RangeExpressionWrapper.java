package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.RangeExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class RangeExpressionWrapper extends ExpressionWrapper {
    private static final int DEFAULT_TO = 0;
    private static final int DEFAULT_FROM = 0;
    private ExpressionWrapper from;
    private ExpressionWrapper to;
    private boolean inclusive = true;

    public RangeExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        from = new ConstantExpressionWrapper(DEFAULT_FROM, this);
        to = new ConstantExpressionWrapper(DEFAULT_TO, this);
    }

    public RangeExpressionWrapper(ExpressionWrapper from, ExpressionWrapper to, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.from = from;
        this.to = to;
    }

    public RangeExpressionWrapper(ExpressionWrapper from, ExpressionWrapper to, boolean inclusive,
            ASTNodeWrapper parentNodeWrapper) {
        this(from, to, parentNodeWrapper);
        this.inclusive = inclusive;
    }

    public RangeExpressionWrapper(RangeExpression rangeExpression, ASTNodeWrapper parentNodeWrapper) {
        super(rangeExpression, parentNodeWrapper);
        this.from = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(rangeExpression.getFrom(), this);
        this.to = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(rangeExpression.getTo(), this);
        this.inclusive = rangeExpression.isInclusive();
    }

    public RangeExpressionWrapper(RangeExpressionWrapper rangeExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(rangeExpressionWrapper, parentNodeWrapper);
        this.from = rangeExpressionWrapper.getFrom().copy(this);
        this.to = rangeExpressionWrapper.getTo().copy(this);
        this.inclusive = rangeExpressionWrapper.isInclusive();
    }

    public ExpressionWrapper getFrom() {
        return from;
    }

    public void setFrom(ExpressionWrapper from) {
        this.from = from;
    }

    public ExpressionWrapper getTo() {
        return to;
    }

    public void setTo(ExpressionWrapper to) {
        this.to = to;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }

    @Override
    public String getText() {
        return "(" + from.getText() + (!isInclusive() ? "..<" : "..") + to.getText() + ")";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(from);
        astNodeWrappers.add(to);
        return astNodeWrappers;
    }

    @Override
    public RangeExpressionWrapper clone() {
        return new RangeExpressionWrapper(this, getParent());
    }
}
