package com.kms.katalon.composer.webui.setting;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.composer.webui.component.RemoteDebugPreferenceComposite;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public abstract class RemoteDebugPreferencePage extends DriverPreferencePage {
	
    protected WebUiDriverConnector remoteDebugDriverConnector;
    protected Button btnConfigDebuger;

    @Override
    public IDriverConnector getDriverConnector(String configurationFolderPath) {
    	return createDriverConnector(configurationFolderPath);
    }

    @Override
	public void contributeButtons(Composite buttonBar) {
		
		((GridLayout)buttonBar.getLayout()).numColumns = ((GridLayout)buttonBar.getLayout()).numColumns + 1;
		
		btnConfigDebuger = new Button(buttonBar, SWT.PUSH);
		btnConfigDebuger.setText("Configure");
		Dialog.applyDialogFont(btnConfigDebuger);
		
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		Point minButtonSize = btnConfigDebuger.computeSize(SWT.DEFAULT,SWT.DEFAULT, true);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		
		data.widthHint = Math.max(widthHint, minButtonSize.x);
		btnConfigDebuger.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		minButtonSize = btnConfigDebuger.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minButtonSize.x);
		btnConfigDebuger.setLayoutData(data);
		
		btnConfigDebuger.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doInstallDebuger();
			}
		});
    }
    
    protected void doInstallDebuger(){}

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
