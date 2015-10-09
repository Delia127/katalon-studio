package com.kms.katalon.composer.execution.components;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.dialog.DriverConnectorListBuilderDialog;
import com.kms.katalon.execution.configuration.CustomRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class DriverConnectorListCellEditor extends DialogCellEditor {
    private String defaultContent;
    private CustomRunConfiguration customRunConfig;

    public DriverConnectorListCellEditor(Composite parent, String defaultContent, CustomRunConfiguration customRunConfig) {
        super(parent);
        this.defaultContent = defaultContent;
        this.customRunConfig = customRunConfig;
        setValidator(new ICellEditorValidator() {

            @Override
            public String isValid(Object value) {
                if (value instanceof List) {
                    return null;
                }
                return StringConstants.INVALID_TYPE_MESSAGE;
            }
        });
    }

    @Override
    protected void updateContents(Object value) {
        if (defaultContent != null) {
            super.updateContents(defaultContent.replace("&", "&&"));
        } else {
            super.updateContents(value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        DriverConnectorListBuilderDialog dialog = new DriverConnectorListBuilderDialog(cellEditorWindow.getShell(),
                (List<IDriverConnector>) getValue(), customRunConfig);
        int result = dialog.open();
        if (result == Dialog.OK) {
            return dialog.getDriverConnectorList();
        }
        return null;
    }
}
