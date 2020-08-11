package com.kms.katalon.composer.testcase.ast.editors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariablePropertyComboBoxCellEditorWithContentProposal extends ComboBoxCellEditorWithContentProposal {

    private Object[] items;

    private PropertyExpressionWrapper parentWrapper;

    private static String GLOBAL_VARIABLE_CLASS_ALIAS_Name = "GlobalVariable";

    public GlobalVariablePropertyComboBoxCellEditorWithContentProposal(Composite parent,
            PropertyExpressionWrapper parentWrapper, Object[] items, Object[] displayedItems, String[] toolTips) {
        super(parent, displayedItems, toolTips);
        this.items = items;
        this.parentWrapper = parentWrapper;
    }

    @Override
    protected Object doGetValue() {
        String variableName = null;

        int selectedIndex = (int) super.doGetValue();
        if (selectedIndex >= 0) {
            Object selectedItem = items[selectedIndex];
            variableName = ((GlobalVariableEntity) selectedItem).getName();
            if (StringUtils.isBlank(variableName)) {
                variableName = null;
            }
        }

        return new PropertyExpressionWrapper(GLOBAL_VARIABLE_CLASS_ALIAS_Name, variableName, parentWrapper);
    }

    @Override
    protected void doSetValue(Object value) {
        if (!(value instanceof PropertyExpressionWrapper)) {
            super.doSetValue(value);
            return;
        }

        PropertyExpressionWrapper variable = (PropertyExpressionWrapper) value;
        String variableName = variable.getPropertyAsString();
        for (int index = 0; index < items.length; index++) {
            if (StringUtils.equals(getVariableName(items[index]), variableName)) {
                super.doSetValue(index);
                return;
            }
        }

        super.doSetValue(-1);
    }

    private String getVariableName(Object selectedItem) {
        if (selectedItem instanceof String) {
            return (String) selectedItem;
        }
        if (selectedItem instanceof GlobalVariableEntity) {
            return ((GlobalVariableEntity) selectedItem).getName();
        }
        return null;
    }
}
