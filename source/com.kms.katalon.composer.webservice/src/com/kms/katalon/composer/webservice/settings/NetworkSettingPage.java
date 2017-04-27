package com.kms.katalon.composer.webservice.settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

public class NetworkSettingPage extends PreferencePageWithHelp {

    private WebServiceSettingStore settingStore;

    private Map<SSLCertificateOption, Button> sslCertButtons;

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
        Composite container = new Composite(parent, SWT.NONE);
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

        initializeInput();
        return container;
    }

    private void initializeInput() {
        try {
            // Set selection option for SSL certification
            SSLCertificateOption option = settingStore.getSSLCertificateOption();
            sslCertButtons.entrySet().stream().forEach(entry -> {
                entry.getValue().setSelection(entry.getKey() == option);
            });
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e,
                    ComposerWebserviceMessageConstants.DIA_MSG_UNABLE_TO_LOAD_NETWORK_PAGE,
                    StringConstants.ERROR_TITLE);
        }
    }

    /**
     * Saves all updated value to setting store (eg. selected {@link SSLCertificateOption});
     */
    @Override
    public boolean performOk() {
        SSLCertificateOption selectedSSLOption = sslCertButtons.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getSelection())
                .findFirst()
                .get()
                .getKey();
        try {
            settingStore.saveSSLCertificateOption(selectedSSLOption);
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
}
