package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;

public class StringConstantCellEditor extends TextCellEditor {
    private ConstantExpressionWrapper constantExpression;
    
    public StringConstantCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof ConstantExpressionWrapper
                && ((ConstantExpressionWrapper) value).getValue() instanceof String);
        constantExpression = (ConstantExpressionWrapper) value;
        super.doSetValue(((ConstantExpressionWrapper) value).getValue());
    }

    @Override
    protected ConstantExpressionWrapper doGetValue() {
        String stringResult = (String) super.doGetValue();
        if (constantExpression == null) {
            return null;
        }
        constantExpression.setValue(stringResult);
        return constantExpression;
    }
}
