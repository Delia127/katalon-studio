package com.kms.katalon.composer.webui.component;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.dialog.DriverConnectorBuilderDialog;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;

public class IEDriverConnectorCellEditor extends DriverConnectorCellEditor {
    public IEDriverConnectorCellEditor(Composite parent) {
        super(parent);
        setValidator(new ICellEditorValidator() {

            @Override
            public String isValid(Object value) {
                if (value instanceof IEDriverConnector) {
                    return null;
                }
                return StringConstants.INVALID_TYPE_MESSAGE;
            }
        });
    }

    @Override
    protected DriverConnectorBuilderDialog getDriverConnectorBuilderDialog() {
        return new WebUiDriverConnectorBuilderDialog(Display.getCurrent().getActiveShell(),
                (IEDriverConnector) getValue());
    }

}
