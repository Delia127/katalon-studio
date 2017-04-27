package com.kms.katalon.core.webservice.common;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

public interface Requestor {

    public ResponseObject send(RequestObject request) throws Exception;

    public default TrustManager[] getTrustManagers() throws IOException {
        SSLCertificateOption sslCertificateOption = WebServiceSettingStore.create(RunConfiguration.getProjectDir())
                .getSSLCertificateOption();
        if (sslCertificateOption == SSLCertificateOption.BYPASS) {
            return new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            } };
        }
        return new TrustManager[0];
    }
    
    public default HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                try {
                    SSLCertificateOption sslCertificateOption = WebServiceSettingStore.create(RunConfiguration.getProjectDir())
                            .getSSLCertificateOption();

                    return sslCertificateOption == SSLCertificateOption.BYPASS;
                } catch (IOException e) {
                    return false;
                }
            }
        };
    }
}
