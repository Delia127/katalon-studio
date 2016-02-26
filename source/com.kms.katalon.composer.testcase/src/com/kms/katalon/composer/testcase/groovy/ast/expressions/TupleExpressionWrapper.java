package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.TupleExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class TupleExpressionWrapper extends ExpressionWrapper {
    protected List<ExpressionWrapper> expressions = new ArrayList<ExpressionWrapper>();

    public TupleExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public TupleExpressionWrapper(List<ExpressionWrapper> expressions, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        setExpressions(expressions);
    }

    public TupleExpressionWrapper(TupleExpression tupleExpression, ASTNodeWrapper parentNodeWrapper) {
        super(tupleExpression, parentNodeWrapper);
        for (Expression expression : tupleExpression.getExpressions()) {
            expressions.add(ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression, this));
        }
    }

    public TupleExpressionWrapper(TupleExpressionWrapper tupleExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(tupleExpressionWrapper, parentNodeWrapper);
        for (ExpressionWrapper expression : tupleExpressionWrapper.getExpressions()) {
            expressions.add(expression.copy(this));
        }
    }

    public List<ExpressionWrapper> getExpressions() {
        return expressions;
    }

    public void addExpression(ExpressionWrapper expression) {
        expressions.add(expression);
    }

    public void setExpressions(List<ExpressionWrapper> expressions) {
        this.expressions = expressions;
    }

    public ExpressionWrapper getExpression(int index) {
        if (index < 0 || index >= expressions.size()) {
            return null;
        }
        return this.expressions.get(index);
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("(");
        boolean first = true;
        for (ExpressionWrapper expression : expressions) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }

            buffer.append(expression.getText());
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(expressions);
        return astNodeWrappers;
    }

    @Override
    public TupleExpressionWrapper clone() {
        return new TupleExpressionWrapper(this, getParent());
    }
}
