package com.kms.katalon.composer.execution.settings;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.DriverPropertySettingStore;
import com.kms.katalon.entity.project.ProjectEntity;

public abstract class DriverPreferencePage extends PreferencePage {
	private Map<String, Object> driverProperties; 
	
	private DriverPropertySettingStore driverPropertySettingStore;

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
        
        DriverPropertyMapComposite control = new DriverPropertyMapComposite(container);

        initilize();
        control.setInput(driverProperties);
        return container;
	}

    private void initilize() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
		driverPropertySettingStore = getDriverPropertySettingStore(projectEntity.getFolderLocation());
		if (driverPropertySettingStore != null) {
	        driverProperties = driverPropertySettingStore.getDriverProperties();
		} else {
		    driverProperties = new LinkedHashMap<String, Object>(driverProperties);
		}
	}
	
	protected abstract DriverPropertySettingStore getDriverPropertySettingStore(String projectFolderLocation);
	
	@Override
	public boolean performOk() {
		ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
		try {
			if (projectEntity == null || driverProperties == null || driverPropertySettingStore == null) {
			    return true;
			}
			driverPropertySettingStore.saveDriverProperties(driverProperties);
			return true;
		} catch (Exception e) {
			MessageDialog.openError(null, StringConstants.ERROR_TITLE, 
					StringConstants.SETT_ERROR_MSG_UNABLE_TO_SAVE_PROJ_SETTS);
			return false;
		}
	}
	
	@Override 
	protected void performDefaults() {
		initilize();
		super.performDefaults();
	}
	
}
