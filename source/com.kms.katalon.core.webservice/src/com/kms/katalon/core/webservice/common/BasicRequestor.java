package com.kms.katalon.core.webservice.common;

import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.core.webservice.exception.WebServiceException;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

public abstract class BasicRequestor implements Requestor {
    private String projectDir;
    private ProxyInformation proxyInformation;

    public BasicRequestor(String projectDir, ProxyInformation proxyInformation) {
        this.projectDir = projectDir;
        this.proxyInformation = proxyInformation;
    }

    private SSLCertificateOption getSslCertificateOption() throws IOException {
        return WebServiceSettingStore.create(projectDir).getSSLCertificateOption();
    }

    protected TrustManager[] getTrustManagers() throws IOException {
        if (getSslCertificateOption() == SSLCertificateOption.BYPASS) {
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
    
    public HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                try {
                    return getSslCertificateOption() == SSLCertificateOption.BYPASS;
                } catch (IOException e) {
                    return false;
                }
            }
        };
    }

    public Proxy getProxy() throws WebServiceException {
        if (proxyInformation == null) {
            return Proxy.NO_PROXY;
        }
        try {
            return ProxyUtil.getProxy(proxyInformation);
        } catch (URISyntaxException e) {
            throw new WebServiceException(e);
        } catch (IOException e) {
            throw new WebServiceException(e);
        }
    }
}
