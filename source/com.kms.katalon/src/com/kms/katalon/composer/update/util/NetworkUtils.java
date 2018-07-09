package com.kms.katalon.composer.update.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NetworkUtils {
    public static HttpURLConnection createURLConnection(String sUrl, Proxy proxy)
            throws IOException, GeneralSecurityException {
        URL url = new URL(sUrl);

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, getTrustManagers(), new java.security.SecureRandom());

        HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
        if (uc instanceof HttpsURLConnection) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

           // ((HttpsURLConnection) uc).setHostnameVerifier(getHostnameVerifier());
        }
        // Set User-Agent for each request to help managing from S3
        // (such as filter out which is valid request or count the download...)
        uc.setRequestProperty("User-Agent", "Katalon Studio");
        uc.setUseCaches(false);
        uc.setDoOutput(true);

        return uc;
    }
    
    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};
    }

    private static HostnameVerifier getHostnameVerifier() {
        return (urlHostName, session) -> true;
    }

}
