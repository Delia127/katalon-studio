package com.kms.katalon.composer.webservice.settings;

import java.io.IOException;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.SSLSettings;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

public class SSLSettingPage extends PreferencePage {
    
    private WebServiceSettingStore settingStore;
    
    private Text txtKeyStore;
    
    private Text txtKeyStorePassword;
    
    private Button btnBrowse;

    public SSLSettingPage() {
        settingStore = WebServiceSettingStore.create(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
    }
    
    @Override
    protected Control createContents(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        body.setLayout(new GridLayout(3, false));
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
        Label lblKeyStore = new Label(body, SWT.NONE);
        lblKeyStore.setText(StringConstants.SSLPreferencePage_LBL_KEYSTORE);
        
        txtKeyStore = new Text(body, SWT.BORDER);
        txtKeyStore.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
       
        btnBrowse = new Button(body, SWT.NONE);
        btnBrowse.setText(StringConstants.SSLPreferencePage_BTN_BROWSE_FILE);
        
        Label lblKeyStorePassword = new Label(body, SWT.NONE);
        lblKeyStorePassword.setText(StringConstants.SSLPreferencePage_LBL_KEYSTORE_PASSWORD);
        
        txtKeyStorePassword = new Text(body, SWT.PASSWORD | SWT.BORDER);
        txtKeyStorePassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        SSLSettings settings = getSSLSettings();
        txtKeyStore.setText(settings.getKeyStoreFile());
        txtKeyStorePassword.setText(settings.getKeyStorePassword());
                
        registerControlListeners();

        return body;
    }
    
    private SSLSettings getSSLSettings() {
        SSLSettings settings;
        try {
            settings = settingStore.getSSLSettings();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            settings = null;
        }
        return settings;
    }
    
    private void registerControlListeners() {
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
                String fileLocation = dialog.open();
                txtKeyStore.setText(fileLocation);
            }
        });
    }
    
    @Override
    public boolean performOk() {
        if (!isControlCreated()) {
            return super.performOk();
        }
        boolean performOk = super.performOk();
        if (performOk) {
            SSLSettings settings = new SSLSettings();
            settings.setKeyStoreFile(txtKeyStore.getText());
            settings.setKeyStorePassword(txtKeyStorePassword.getText());
            try {
                settingStore.saveSSLSettings(settings);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
        return performOk;
    }
    
    

}
