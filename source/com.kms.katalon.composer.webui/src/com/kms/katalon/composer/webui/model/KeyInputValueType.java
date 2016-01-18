package com.kms.katalon.composer.webui.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;

public class KeyInputValueType implements ICustomInputValueType {

    private static final String NAME = "Key";

    public static final String ENTER_KEY_ENUM = "ENTER";

    private String[] keyEnumStringList;

    @Override
    public CellEditor getCellEditorForValue(Composite parent, Object astObject, ClassNode scriptClass) {
        keyEnumStringList = getKeyEnumStringList();
        return new ComboBoxCellEditor(parent, keyEnumStringList);
    }

    @Override
    public boolean isEditable(Object astObject, ClassNode scriptClass) {
        if (astObject instanceof PropertyExpression) {
            PropertyExpression propertyExpression = (PropertyExpression) astObject;
            String objectExpressionString = propertyExpression.getObjectExpression().getText();
            if (objectExpressionString.equals(Keys.class.getName()) || objectExpressionString.equals(Keys.class
                    .getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getReadableName() {
        return getName();
    }

    @Override
    public String[] getTags() {
        return new String[] {};
    }

    @Override
    public Object getNewValue(Object existingValue) {
        return new PropertyExpression(new ClassExpression(new ClassNode(Keys.class)), ENTER_KEY_ENUM);
    }

    @Override
    public Object getValueToEdit(Object astObject, ClassNode scriptClass) {
        if (astObject instanceof PropertyExpression) {
            String propertyName = ((PropertyExpression) astObject).getPropertyAsString();
            for (int i = 0; i < keyEnumStringList.length; i++) {
                if (keyEnumStringList[i].equalsIgnoreCase(propertyName)) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public Object changeValue(Object astObject, Object newValue, ClassNode scriptClass) {
        if (astObject instanceof PropertyExpression && newValue instanceof Integer) {
            int keyEnumIndex = (int) newValue;
            if (keyEnumIndex >= 0 && keyEnumIndex <= keyEnumStringList.length) {
                String oldPropertyName = ((PropertyExpression) astObject).getPropertyAsString();
                String newPropertyName = keyEnumStringList[keyEnumIndex];
                if (oldPropertyName.equalsIgnoreCase(newPropertyName)) {
                    return astObject;
                }
                return new PropertyExpression(new ClassExpression(new ClassNode(Keys.class)), newPropertyName);
            }
        }
        return null;
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
    public String getDisplayValue(Object astObject) {
        return AstTreeTableTextValueUtil.getInstance().getTextValue(astObject);
    }
}
