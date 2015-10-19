package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.composer.webui.component.RemoteWebDriverPreferenceComposite;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteWebPreferencePage extends DriverPreferencePage {
    private RemoteWebDriverConnector remoteDriverConnector;
    private RemoteWebDriverPreferenceComposite remoteUrlComposite;

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
        
        remoteUrlComposite = new RemoteWebDriverPreferenceComposite(container, SWT.NONE, remoteDriverConnector);
        return container;
    }

    @Override
    public boolean performOk() {
        if (remoteUrlComposite != null) {
            remoteDriverConnector = (RemoteWebDriverConnector) remoteUrlComposite.getResult();
        }
        return super.performOk();
    }
}
