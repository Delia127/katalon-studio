package com.kms.katalon.composer.testcase.ast.editors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.openqa.selenium.Keys;

public class KeyInputComboBoxCellEditor extends PropertyComboBoxCellEditor {

    private static String[] keyNames;

    public KeyInputComboBoxCellEditor(Composite parent) {
        super(parent, getKeyEnumStringList());
    }

    private static String[] getKeyEnumStringList() {
        if (keyNames != null) {
            return keyNames;
        }

        List<String> keysEnumList = new ArrayList<String>();
        for (Field field : Keys.class.getDeclaredFields()) {
            if (field.isEnumConstant()) {
                keysEnumList.add(field.getName());
            }
        }

        keyNames = keysEnumList.toArray(new String[keysEnumList.size()]);
        Arrays.sort(keyNames);
        return keyNames;
    }

}
