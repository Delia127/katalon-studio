package com.kms.katalon.composer.webui.component;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteWebDriverConnectorBuilderDialog extends WebUiDriverConnectorBuilderDialog {
    private RemoteWebDriverPreferenceComposite driverPreferenceComposite;

    public RemoteWebDriverConnectorBuilderDialog(Shell parentShell, RemoteWebDriverConnector remoteWebDriverConnector) {
        super(parentShell, remoteWebDriverConnector);
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
        driverPreferenceComposite = new RemoteWebDriverPreferenceComposite(container,
                SWT.NONE, (RemoteWebDriverConnector) webUiDriverConnector);
        return container;
    }

    @Override
    public IDriverConnector getResult() {
        return driverPreferenceComposite.getResult();
    }
}
