package com.kms.katalon.composer.webservice.settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;
import com.kms.katalon.license.models.LicenseType;

public class NetworkSettingPage extends PreferencePageWithHelp {

    private WebServiceSettingStore settingStore;

    private Map<SSLCertificateOption, Button> sslCertButtons;

    private Composite container;
    
    private Text txtKeyStore;
    
    private Text txtKeyStorePassword;
    
    private Button btnBrowse;

    private GridData gdClientCert;

    public NetworkSettingPage() {
        settingStore = WebServiceSettingStore
                .create(ProjectController.getInstance().getCurrentProject().getFolderLocation());
        sslCertButtons = new HashMap<>();
    }

    /**
     * Creates page contents for network settings.
     */
    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group grpSSLCertOptions = new Group(container, SWT.NONE);
        grpSSLCertOptions.setText(ComposerWebserviceMessageConstants.DIA_GRP_LBL_CERTITICATES);
        grpSSLCertOptions.setLayout(new GridLayout(1, false));
        grpSSLCertOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button rbtnNoneCertificateOpt = new Button(grpSSLCertOptions, SWT.RADIO);
        rbtnNoneCertificateOpt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        rbtnNoneCertificateOpt.setText(ComposerWebserviceMessageConstants.DIA_LBL_SSL_CERT_NONE_OPT);
        sslCertButtons.put(SSLCertificateOption.NONE, rbtnNoneCertificateOpt);

        Button rbtnBypassCertificateOpt = new Button(grpSSLCertOptions, SWT.RADIO);
        rbtnBypassCertificateOpt.setText(ComposerWebserviceMessageConstants.DIA_LBL_SSL_CERT_BYPASS_OPT);
        sslCertButtons.put(SSLCertificateOption.BYPASS, rbtnBypassCertificateOpt);

        Composite compositeClientCert = new Composite(container, SWT.NONE);
        gdClientCert = new GridData(SWT.FILL, SWT.FILL, true, false);
        compositeClientCert.setLayoutData(gdClientCert);

        compositeClientCert.setLayout(new FillLayout());
        Group grpClientCert = new Group(compositeClientCert, SWT.NONE);
        grpClientCert.setText(ComposerWebserviceMessageConstants.DIA_GRP_LBL_CLIENT_CERTIFICATES);
        grpClientCert.setLayout(new GridLayout(3, false));

        Label lblKeyStore = new Label(grpClientCert, SWT.NONE);
        lblKeyStore.setText(ComposerWebserviceMessageConstants.DIA_LBL_KEYSTORE);
        
        txtKeyStore = new Text(grpClientCert, SWT.BORDER);
        txtKeyStore.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        btnBrowse = new Button(grpClientCert, SWT.NONE);
        btnBrowse.setText(ComposerWebserviceMessageConstants.DIA_BTN_BROWSE_FILE);
        
        Label lblKeyStorePassword = new Label(grpClientCert, SWT.NONE);
        lblKeyStorePassword.setText(ComposerWebserviceMessageConstants.DIA_LBL_KEYSTORE_PASSWORD);
        
        txtKeyStorePassword = new Text(grpClientCert, SWT.PASSWORD | SWT.BORDER);
        txtKeyStorePassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        registerControlListeners();
        
        initializeInput();
        
        return container;
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
    
    private void initializeInput() {
        try {
            // Set selection option for SSL certification
            SSLCertificateOption option = settingStore.getSSLCertificateOption();
            sslCertButtons.entrySet().stream().forEach(entry -> {
                entry.getValue().setSelection(entry.getKey() == option);
            });
            
            SSLClientCertificateSettings clientCertSettings = settingStore.getClientCertificateSettings();
            txtKeyStore.setText(clientCertSettings.getKeyStoreFile());
            txtKeyStorePassword.setText(clientCertSettings.getKeyStorePassword());

            // Hide this feature for normal users
            if (LicenseType.valueOf(
                    ApplicationInfo.getAppProperty(ApplicationStringConstants.LICENSE_TYPE)) == LicenseType.FREE) {
                gdClientCert.heightHint = 0;
                container.layout(true);
            }
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e,
                    ComposerWebserviceMessageConstants.DIA_MSG_UNABLE_TO_LOAD_NETWORK_PAGE,
                    StringConstants.ERROR_TITLE);
        }
    }

    private boolean isInitialized() {
        return container != null;
    }

    /**
     * Saves all updated value to setting store (eg. selected {@link SSLCertificateOption});
     */
    @Override
    public boolean performOk() {
        if (!isInitialized()) {
            return true;
        }

        SSLCertificateOption selectedSSLOption = sslCertButtons.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getSelection())
                .findFirst()
                .get()
                .getKey();
        try {
            settingStore.saveSSLCertificateOption(selectedSSLOption);
            
            SSLClientCertificateSettings clientCertSettings = new SSLClientCertificateSettings();
            clientCertSettings.setKeyStoreFile(txtKeyStore.getText());
            clientCertSettings.setKeyStorePassword(txtKeyStorePassword.getText());
            settingStore.saveClientCerfiticateSettings(clientCertSettings);
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e,
                    ComposerWebserviceMessageConstants.DIA_MSG_UNABLE_TO_UPDATE_NETWORK_PAGE,
                    StringConstants.ERROR_TITLE);
            return false;
        }
    }

    /**
     * Restores defaults value of network page
     */
    @Override
    protected void performDefaults() {
        initializeInput();
    }
    
    @Override
    public boolean hasDocumentation() {
        return true;
    }
    
    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTING_NETWORK;
    }
}
