package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;

public class BooleanConstantComboBoxCellEditor extends ComboBoxCellEditor {
    private ConstantExpressionWrapper constantExpression;
    private static final String[] BOOLEAN_CONSTANTS = new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() };

    public BooleanConstantComboBoxCellEditor(Composite parent) {
        super(parent, BOOLEAN_CONSTANTS);
    }

    /**
     * The <code>PropertyComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method
     * returns the edited PropertyExpressionWrapper
     *
     * @return the edited PropertyExpressionWrapper
     */
    @Override
    protected ConstantExpressionWrapper doGetValue() {
        Integer selectionIndex = (Integer) super.doGetValue();
        if (selectionIndex < 0 || selectionIndex >= getItems().length) {
            return null;
        }
        constantExpression.setValue(Boolean.parseBoolean(getItems()[selectionIndex]));
        return constantExpression;
    }

    /**
     * The <code>PropertyComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method
     * accepts a PropertyExpressionWrapper object
     *
     * @param value
     *            the PropertyExpressionWrapper object
     */
    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof ConstantExpressionWrapper
                && (((ConstantExpressionWrapper) value).isTrueExpression() || ((ConstantExpressionWrapper) value).isFalseExpression()));
        constantExpression = (ConstantExpressionWrapper) value;
        String propertyEnumValue = constantExpression.getValue().toString();
        for (int index = 0; index < getItems().length; index++) {
            if (!getItems()[index].equals(propertyEnumValue)) {
                continue;
            }
            super.doSetValue(index);
            break;
        }
        return;
    }
}
