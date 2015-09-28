package com.kms.katalon.composer.execution.components;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.execution.dialog.ListPropertyValueBuilderDialog;

public class ListPropertyValueCellEditor extends DialogCellEditor {
    private static final String INVALID_TYPE_MESSAGE = "Invalid Type";
    
    public ListPropertyValueCellEditor(Composite parent) {
        super(parent);
        setValidator(new ICellEditorValidator() {
            
            @Override
            public String isValid(Object value) {
                if (value instanceof List) {
                    return null;
                }
                return INVALID_TYPE_MESSAGE;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        ListPropertyValueBuilderDialog dialog = new ListPropertyValueBuilderDialog(cellEditorWindow.getShell(), (List<Object>) getValue());
        int result = dialog.open();
        if (result == Dialog.OK) {
            return dialog.getPropertyList();
        }
        return null;
    }

}
