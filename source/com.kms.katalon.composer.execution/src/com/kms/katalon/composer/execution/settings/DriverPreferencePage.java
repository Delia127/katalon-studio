package com.kms.katalon.composer.execution.settings;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.IDriverConnector;

public abstract class DriverPreferencePage extends PreferencePageWithHelp {
    protected DriverPreferenceComposite driverPreferenceComposite;

    protected IDriverConnector driverConnector;

    public DriverPreferencePage() {
        initilize();
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

        driverPreferenceComposite = new DriverPreferenceComposite(container, SWT.NONE, driverConnector);    
        
        updateInput();
        
        return container;
    }
    
    protected void updateInput() {
        driverPreferenceComposite.setInput(driverConnector.getUserConfigProperties());
    }

    protected void initilize() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        driverConnector = getDriverConnector(projectEntity.getFolderLocation() + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
    }

    protected abstract IDriverConnector getDriverConnector(String configurationFolderPath);

    @Override
    public boolean performOk() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        try {
            if (projectEntity == null || driverPreferenceComposite == null || driverPreferenceComposite.getResult() == null
                    || driverConnector == null) {
                return true;
            }
            driverConnector = driverPreferenceComposite.getResult();
            driverConnector.saveUserConfigProperties();
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
        updateInput();
        super.performDefaults();
    }
    
    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EXECUTION;
    }
}
