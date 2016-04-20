package com.kms.katalon.composer.testcase.ast.editors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.openqa.selenium.Keys;

public class KeyInputComboBoxCellEditor extends PropertyComboBoxCellEditor {
    public KeyInputComboBoxCellEditor(Composite parent) {
        super(parent, getKeyEnumStringList());
    }

    private static String[] getKeyEnumStringList() {
        List<String> keysEnumList = new ArrayList<String>();
        for (Field field : Keys.class.getDeclaredFields()) {
            if (field.isEnumConstant()) {
                keysEnumList.add(field.getName());
            }
        }
        return keysEnumList.toArray(new String[keysEnumList.size()]);
    }
}
