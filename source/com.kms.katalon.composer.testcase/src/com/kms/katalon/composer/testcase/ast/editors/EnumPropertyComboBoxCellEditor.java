package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.dialogs.ApplyingEditingValue;

public class EnumPropertyComboBoxCellEditor extends PropertyComboBoxCellEditor implements ApplyingEditingValue {
    public EnumPropertyComboBoxCellEditor(Composite parent, Class<?> enumClass) {
        super(parent, new String[0]);
        Object[] enumConstants = enumClass.getEnumConstants();
        String[] enumValues = new String[enumConstants.length];
        for (int index = 0; index < enumConstants.length; index++) {
            enumValues[index] = enumConstants[index].toString();
        }
        setItems(enumValues);
    }
    
    public void applyEditingValue() {
        fireApplyEditorValue();
    }
}
