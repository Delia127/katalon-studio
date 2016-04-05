package com.kms.katalon.composer.testcase.ast.editors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;

public class KeyInputComboBoxCellEditor extends ComboBoxCellEditor {
    private PropertyExpressionWrapper propertyExpresion;

    private String[] keyEnumStringList;

    public KeyInputComboBoxCellEditor(Composite parent) {
        super(parent, new String[0]);
        keyEnumStringList = getKeyEnumStringList();
        setItems(keyEnumStringList);
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

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof PropertyExpressionWrapper);
        propertyExpresion = (PropertyExpressionWrapper) value;
        for (int index = 0; index < keyEnumStringList.length; index++) {
            if (keyEnumStringList[index].equals(propertyExpresion.getPropertyAsString())) {
                super.doSetValue(index);
                return;
            }
        }
        super.doSetValue(0);
    }

    @Override
    protected PropertyExpressionWrapper doGetValue() {
        Integer selectionIndex = (Integer) super.doGetValue();
        if (selectionIndex < 0 || selectionIndex >= keyEnumStringList.length) {
            return null;
        }
        propertyExpresion.setProperty(keyEnumStringList[selectionIndex]);
        return propertyExpresion;
    }
}
