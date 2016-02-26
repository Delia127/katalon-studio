package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.Expression;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class ArrayExpressionWrapper extends ExpressionWrapper {
    private List<ExpressionWrapper> expressions = new ArrayList<ExpressionWrapper>();
    private List<ExpressionWrapper> sizeExpression = new ArrayList<ExpressionWrapper>();
    private ClassNodeWrapper elementType;

    public ArrayExpressionWrapper(ArrayExpression arrayExpression, ASTNodeWrapper parentNodeWrapper) {
        super(arrayExpression, parentNodeWrapper);
        for (Expression expression : arrayExpression.getExpressions()) {
            expressions.add(ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression, this));
        }
        for (Expression expression : arrayExpression.getSizeExpression()) {
            sizeExpression.add(ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression, this));
        }
        elementType = new ClassNodeWrapper(arrayExpression.getElementType(), this);
    }

    public ArrayExpressionWrapper(ArrayExpressionWrapper arrayExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(arrayExpressionWrapper, parentNodeWrapper);
        for (ExpressionWrapper expression : arrayExpressionWrapper.getExpressions()) {
            expressions.add(expression.copy(this));
        }
        for (ExpressionWrapper expression : arrayExpressionWrapper.getSizeExpression()) {
            sizeExpression.add(expression.copy(this));
        }
        elementType = new ClassNodeWrapper(arrayExpressionWrapper.getElementType(), this);
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

    public ClassNodeWrapper getElementType() {
        return elementType;
    }

    public List<ExpressionWrapper> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<ExpressionWrapper> expressions) {
        this.expressions = expressions;
    }

    public List<ExpressionWrapper> getSizeExpression() {
        return sizeExpression;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(expressions);
        astNodeWrappers.addAll(sizeExpression);
        return astNodeWrappers;
    }

    @Override
    public ArrayExpressionWrapper clone() {
        return new ArrayExpressionWrapper(this, parentNodeWrapper);
    }
}
