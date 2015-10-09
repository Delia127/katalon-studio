package com.kms.katalon.composer.mobile.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.dialog.DriverConnectorBuilderDialog;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;

public class MobileDriverConnectorBuilderDialog extends DriverConnectorBuilderDialog {
    private MobileDriverConnector mobileDriverConnector;
    private MobileDriverPreferenceComposite driverPreferenceComposite;

    public MobileDriverConnectorBuilderDialog(Shell parentShell, MobileDriverConnector mobileDriverConnector) {
        super(parentShell);
        this.mobileDriverConnector = mobileDriverConnector;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        driverPreferenceComposite = new MobileDriverPreferenceComposite(container,
                SWT.NONE, mobileDriverConnector);
        return container;
    }

    @Override
    public IDriverConnector getResult() {
        return driverPreferenceComposite.getResult();
    }
}
