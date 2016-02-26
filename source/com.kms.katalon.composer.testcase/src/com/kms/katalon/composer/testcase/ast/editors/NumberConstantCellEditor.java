package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.syntax.Numbers;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.editors.NumberCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;

public class NumberConstantCellEditor extends NumberCellEditor {
    private ConstantExpressionWrapper constantExpression;
    
    public NumberConstantCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof ConstantExpressionWrapper
                && ((ConstantExpressionWrapper) value).getValue() instanceof Number);
        constantExpression = (ConstantExpressionWrapper) value;
        super.doSetValue(String.valueOf(((ConstantExpressionWrapper) value).getValue()));
    }

    @Override
    protected ConstantExpressionWrapper doGetValue() {
        String stringResult = (String) super.doGetValue();
        if (stringResult == null || stringResult.isEmpty()) {
            return null;
        }
        Number numValue = 0;
        try {
            // try integer number first, if false then use decimal
            numValue = Numbers.parseInteger((String) stringResult);
        } catch (NumberFormatException e) {
            numValue = Numbers.parseDecimal((String) stringResult);
        }
        constantExpression.setValue(numValue);
        return constantExpression;
    }
}
