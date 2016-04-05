package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class ListExpressionWrapper extends ExpressionWrapper {
    protected List<ExpressionWrapper> expressions = new ArrayList<ExpressionWrapper>();
    protected boolean wrapped = false;

    public ListExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ListExpressionWrapper(List<ExpressionWrapper> expressions, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        setExpressions(expressions);
    }

    public ListExpressionWrapper(ListExpression listExpression, ASTNodeWrapper parentNodeWrapper) {
        super(listExpression, parentNodeWrapper);
        for (Expression childExpression : listExpression.getExpressions()) {
            expressions.add(ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(childExpression, this));
        }
        wrapped = listExpression.isWrapped();
    }

    public ListExpressionWrapper(ListExpressionWrapper listExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(listExpressionWrapper, parentNodeWrapper);
        for (ExpressionWrapper childExpression : listExpressionWrapper.getExpressions()) {
            expressions.add(childExpression.copy(this));
        }
        wrapped = listExpressionWrapper.isWrapped();
    }

    public List<ExpressionWrapper> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<ExpressionWrapper> expressions) {
        this.expressions = expressions;
    }

    public boolean isWrapped() {
        return wrapped;
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getText() {
        StringBuilder value = new StringBuilder();
        value.append("[");
        value.append(StringUtils.join(Iterables.transform(expressions, new Function<ExpressionWrapper, String>() {
            @Override
            public String apply(ExpressionWrapper expression) {
                return expression.getText();
            }
        }).iterator(), ", "));
        value.append("]");
        return value.toString();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(expressions);
        return astNodeWrappers;
    }

    @Override
    public ListExpressionWrapper clone() {
        return new ListExpressionWrapper(this, getParent());
    }
}
