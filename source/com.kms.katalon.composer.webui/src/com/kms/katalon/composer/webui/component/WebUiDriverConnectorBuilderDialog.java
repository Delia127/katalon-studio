package com.kms.katalon.composer.webui.component;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.dialog.DriverConnectorBuilderDialog;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public class WebUiDriverConnectorBuilderDialog extends DriverConnectorBuilderDialog {
    protected WebUiDriverConnector webUiDriverConnector;
    private DriverPreferenceComposite driverPreferenceComposite;

    public WebUiDriverConnectorBuilderDialog(Shell parentShell, WebUiDriverConnector webUiDriverConnector) {
        super(parentShell);
        this.webUiDriverConnector = (WebUiDriverConnector) webUiDriverConnector.clone();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(container);
        driverPreferenceComposite = new DriverPreferenceComposite(container, SWT.NONE, webUiDriverConnector);
        return container;
    }

    @Override
    public IDriverConnector getResult() {
        return driverPreferenceComposite.getResult();
    }
}
