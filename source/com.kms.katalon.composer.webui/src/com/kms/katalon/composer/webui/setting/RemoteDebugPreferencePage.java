package com.kms.katalon.composer.webui.setting;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.composer.webui.component.RemoteDebugPreferenceComposite;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public abstract class RemoteDebugPreferencePage extends DriverPreferencePage {
	
    protected WebUiDriverConnector remoteDebugDriverConnector;

    @Override
    public IDriverConnector getDriverConnector(String configurationFolderPath) {
    	return createDriverConnector(configurationFolderPath);
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
        
        driverPreferenceComposite = new RemoteDebugPreferenceComposite(container, SWT.NONE, remoteDebugDriverConnector);
        
        return container;
    }
    
    protected abstract WebUiDriverConnector createDriverConnector(String configurationFolderPath);
}
