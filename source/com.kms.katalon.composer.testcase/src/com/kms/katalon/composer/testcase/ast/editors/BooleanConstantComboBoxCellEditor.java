package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.dialogs.ApplyingEditingValue;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;

public class BooleanConstantComboBoxCellEditor extends ComboBoxCellEditor implements ApplyingEditingValue {
    private ConstantExpressionWrapper constantExpression;

    private static final String[] BOOLEAN_CONSTANTS = new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() };

    public BooleanConstantComboBoxCellEditor(Composite parent) {
        super(parent, BOOLEAN_CONSTANTS);
    }

    /**
     * The <code>BooleanConstantComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method
     * returns the edited ConstantExpressionWrapper
     *
     * @return the edited ConstantExpressionWrapper
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
     * The <code>BooleanConstantComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method
     * accepts a ConstantExpressionWrapper object
     *
     * @param value the ConstantExpressionWrapper object
     */
    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof ConstantExpressionWrapper
                && ((ConstantExpressionWrapper) value).isBooleanExpression());
        constantExpression = ((ConstantExpressionWrapper) value).clone();
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
    
    public void applyEditingValue() {
        fireApplyEditorValue();
    }
}
