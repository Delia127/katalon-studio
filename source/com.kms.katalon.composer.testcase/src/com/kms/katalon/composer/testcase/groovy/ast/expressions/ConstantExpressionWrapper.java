package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import org.codehaus.groovy.ast.expr.ConstantExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class ConstantExpressionWrapper extends ExpressionWrapper {
    private Object value;

    public ConstantExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ConstantExpressionWrapper(Object value, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        setValue(value);
    }

    public ConstantExpressionWrapper(ConstantExpression constantExpression, ASTNodeWrapper parentNodeWrapper) {
        super(constantExpression, parentNodeWrapper);
        setValue(constantExpression.getValue());
    }

    public ConstantExpressionWrapper(ConstantExpressionWrapper constantExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(constantExpressionWrapper, parentNodeWrapper);
        setValue(constantExpressionWrapper.getValue());
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        if (value != null) {
            this.type.setType(value.getClass());
        }
    }

    @Override
    public String getText() {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + value.toString() + "\"";
        } else if (value instanceof Character) {
            return "'" + value.toString() + "'";
        }
        return value.toString();
    }

    @Override
    public ConstantExpressionWrapper clone() {
        return new ConstantExpressionWrapper(this, getParent());
    }

    public boolean isNullExpression() {
        return value == null;
    }

    public boolean isNumberExpression() {
        return value instanceof Number;
    }

    public boolean isTrueExpression() {
        return Boolean.TRUE.equals(value);
    }

    public boolean isFalseExpression() {
        return Boolean.FALSE.equals(value);
    }
}
