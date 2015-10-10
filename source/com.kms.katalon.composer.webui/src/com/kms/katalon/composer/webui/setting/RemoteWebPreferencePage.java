package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteWebPreferencePage extends DriverPreferencePage {
    private Text txtRemoteServerUrl;
    private RemoteWebDriverConnector remoteDriverConnector;

    @Override
    protected IDriverConnector getDriverConnector(String configurationFolderPath) {
        try {
            remoteDriverConnector = new RemoteWebDriverConnector(configurationFolderPath);
            return remoteDriverConnector;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            // IO Errors, return null
            return null;
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite remoteUrlComposite = new Composite(container, SWT.NONE);
        remoteUrlComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        remoteUrlComposite.setLayout(new GridLayout(2, false));

        Label lblRemoteServerUrl = new Label(remoteUrlComposite, SWT.NONE);
        lblRemoteServerUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblRemoteServerUrl.setText(StringConstants.LBL_REMOTE_SERVER_URL);

        txtRemoteServerUrl = new Text(remoteUrlComposite, SWT.BORDER);
        txtRemoteServerUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtRemoteServerUrl.setText(remoteDriverConnector.getRemoteServerUrl());
        
        driverPreferenceComposite = new DriverPreferenceComposite(container, SWT.NONE, driverConnector);
        driverPreferenceComposite.setInput(driverConnector.getDriverProperties());
        return container;
    }

    @Override
    public boolean performOk() {
        if (remoteDriverConnector != null && txtRemoteServerUrl != null) {
            remoteDriverConnector.setRemoteServerUrl(txtRemoteServerUrl.getText());
        }
        return super.performOk();
    }

}
