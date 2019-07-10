package com.kms.katalon.composer.windows.preferences;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.windows.driver.WindowsDriverFactory;
import com.kms.katalon.execution.windows.WindowsDriverConnector;

public class WindowsExecutionSettingPage extends PreferencePage {

    private DriverPropertyMapComposite driverPropertyMapComposite;

    private WindowsDriverConnector windowsDriverConnector;

    private Text txtUrl;

    @Override
    protected Control createContents(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(1, false));

        Composite driverUrlCompsite = new Composite(mainComposite, SWT.NONE);
        driverUrlCompsite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        driverUrlCompsite.setLayout(new GridLayout(2, false));

        Label lblUrl = new Label(driverUrlCompsite, SWT.NONE);
        lblUrl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblUrl.setText("WinAppDriver URL");

        txtUrl = new Text(driverUrlCompsite, SWT.BORDER);
        txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite desiredCapabilitiesComposite = new Composite(mainComposite, SWT.NONE);
        desiredCapabilitiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        desiredCapabilitiesComposite.setLayout(new GridLayout(1, false));

        Label lblDesiredCapabilities = new Label(desiredCapabilitiesComposite, SWT.NONE);
        lblDesiredCapabilities.setText("Desired Capabilities");
        ControlUtils.setFontToBeBold(lblDesiredCapabilities);

        driverPropertyMapComposite = new DriverPropertyMapComposite(desiredCapabilitiesComposite);

        setInput();

        return mainComposite;
    }

    private void setInput() {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        try {
            windowsDriverConnector = new WindowsDriverConnector(
                    projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);

            txtUrl.setText(windowsDriverConnector.getWinAppDriverUrl());
            driverPropertyMapComposite.setInput(new HashMap<>(windowsDriverConnector.getDesiredCapabilities()));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        if (!isControlCreated()) {
            return true;
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put(WindowsDriverFactory.WIN_APP_DRIVER_PROPERTY, txtUrl.getText());
        properties.put(WindowsDriverFactory.DESIRED_CAPABILITIES_PROPERTY,
                driverPropertyMapComposite.getDriverProperties());
        windowsDriverConnector.setUserConfigProperties(properties);

        try {
            windowsDriverConnector.saveUserConfigProperties();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, GlobalStringConstants.ERROR,
                    "Unable to save Windows Desired Capabilities setting");
        }
        return true;
    }
}
