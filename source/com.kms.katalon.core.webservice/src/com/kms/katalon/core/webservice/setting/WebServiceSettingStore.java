package com.kms.katalon.core.webservice.setting;

import java.io.IOException;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.webservice.constants.StringConstants;

/**
 * Provides settings option for WebService bundle.
 *
 */
public class WebServiceSettingStore extends BundleSettingStore {

    private WebServiceSettingStore(String projectDir) {
        super(projectDir, StringConstants.WEBSERVICE_BUNDLE_ID, false);
    }

    public static WebServiceSettingStore create(String projectDir) {
        return new WebServiceSettingStore(projectDir);
    }

    public SSLCertificateOption getSSLCertificateOption() throws IOException {
        return SSLCertificateOption
                .valueOf(getString(StringConstants.SETTING_SSL_CERTIFICATE, SSLCertificateOption.BYPASS.name()));
    }
    
    public void saveSSLCertificateOption(SSLCertificateOption option) throws IOException {
        setProperty(StringConstants.SETTING_SSL_CERTIFICATE, option.name());
    }
}
