package com.kms.katalon.composer.execution.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.configuration.IDriverConnector;

public abstract class DriverConnectorBuilderDialog extends Dialog {

    public DriverConnectorBuilderDialog(Shell parentShell) {
        super(parentShell);
    }
    
    public abstract IDriverConnector getResult();
    
    @Override
    protected Point getInitialSize() {
        return new Point(700, 500);
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_DRIVER_CONNECTOR_BUILDER);
    }
}
