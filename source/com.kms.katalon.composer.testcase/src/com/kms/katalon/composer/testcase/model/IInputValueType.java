package com.kms.katalon.composer.testcase.model;

import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

public interface IInputValueType {
    public String getName();

    public boolean isEditable(Object astObject, ClassNode scriptClass);

    public CellEditor getCellEditorForValue(Composite parent, Object astObject, ClassNode scriptClass);

    public Object getNewValue(Object existingValue);

    public Object getValueToEdit(Object astObject, ClassNode scriptClass);

    public Object changeValue(Object astObject, Object newValue, ClassNode scriptClass);

    public String getDisplayValue(Object astObject);
}
