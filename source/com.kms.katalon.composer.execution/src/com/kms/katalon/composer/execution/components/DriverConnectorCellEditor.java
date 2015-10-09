package com.kms.katalon.composer.execution.components;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.dialog.DriverConnectorBuilderDialog;
import com.kms.katalon.execution.configuration.IDriverConnector;

public abstract class DriverConnectorCellEditor extends DialogCellEditor {
    
    public DriverConnectorCellEditor(Composite parent) {
        super(parent, SWT.NONE);
        setValidator(new ICellEditorValidator() {
            
            @Override
            public String isValid(Object value) {
                if (value instanceof IDriverConnector) {
                    return null;
                }
                return StringConstants.INVALID_TYPE_MESSAGE;
            }
        });
    }
    
    @Override
    protected void updateContents(Object value) {
        super.updateContents(value);
    }
    
    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        DriverConnectorBuilderDialog dialog = getDriverConnectorBuilderDialog();
        int result = dialog.open();
        if (result == Dialog.OK) {
            return dialog.getResult();
        }
        return null;
    }
    
    protected abstract DriverConnectorBuilderDialog getDriverConnectorBuilderDialog();

}
