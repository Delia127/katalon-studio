package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;

public class EnumPropertyComboBoxCellEditor extends PropertyComboBoxCellEditor {
    public EnumPropertyComboBoxCellEditor(Composite parent, Class<?> enumClass) {
        super(parent, new String[0]);
        Object[] enumConstants = enumClass.getEnumConstants();
        String[] enumValues = new String[enumConstants.length];
        for (int index = 0; index < enumConstants.length; index++) {
            enumValues[index] = enumConstants[index].toString();
        }
        setItems(enumValues);
    }
}
