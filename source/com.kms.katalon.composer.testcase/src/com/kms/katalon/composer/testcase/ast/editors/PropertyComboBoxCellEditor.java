package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;

public abstract class PropertyComboBoxCellEditor extends ComboBoxCellEditor {
    private PropertyExpressionWrapper propertyExpression;

    public PropertyComboBoxCellEditor(Composite parent, String[] items) {
        super(parent, items);
    }

    /**
     * The <code>PropertyComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method
     * returns the edited PropertyExpressionWrapper
     *
     * @return the edited PropertyExpressionWrapper
     */
    @Override
    protected PropertyExpressionWrapper doGetValue() {
        Integer selectionIndex = (Integer) super.doGetValue();
        if (selectionIndex < 0 || selectionIndex >= getItems().length) {
            return null;
        }
        propertyExpression.setProperty(getItems()[selectionIndex]);
        return propertyExpression;
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
        Assert.isTrue(value instanceof PropertyExpressionWrapper);
        propertyExpression = (PropertyExpressionWrapper) value;
        String propertyEnumValue = propertyExpression.getPropertyAsString();
        for (int index = 0; index < getItems().length; index++) {
            if (!getItems()[index].equals(propertyEnumValue)) {
                continue;
            }
            super.doSetValue(index);
            break;
        }
    }
}
