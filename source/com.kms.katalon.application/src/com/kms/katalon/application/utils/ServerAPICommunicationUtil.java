package com.kms.katalon.application.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.logging.LogUtil;

public class ServerAPICommunicationUtil {
    private static final String DEVELOPMENT_URL_API = "https://backend-dev.katalon.com/api";

    private static final String PRODUCTION_URL_API = "https://update.katalon.com/api";
    
    private static final String PRODUCTION_QTEST_ACTIVATION_URL_API = "http://192.168.37.68:3000";
    
    private static final String DEVELOPMENT_QTEST_ACTIVATION_URL_API = "http://192.168.37.68:3000";

    private static final String POST = "POST";

    private static final String GET = "GET";
    
    private static final String PUT = "PUT";
    
    private static final String HTTP ="http";

    public static String post(String function, String jsonData) throws IOException, GeneralSecurityException {
        return invoke(POST, function, jsonData);
    }

    public static String put(String function, String jsonData) throws IOException, GeneralSecurityException {
        return invoke(PUT, function, jsonData);
    }
    
    public static String invoke(String method, String function, String jsonData)
            throws IOException, GeneralSecurityException {
        HttpURLConnection connection = null;
        try {
            connection = createConnection(method, getAPIUrl() + function, ApplicationProxyUtil.getProxy());
            String result = sendAndReceiveData(connection, jsonData);
            LogUtil.printOutputLine(ApplicationMessageConstants.REQUEST_COMPLETED);
            return result;
        } catch (Exception ex) {
            LogUtil.logError(ex);
            return retryInvoke(method, function, jsonData);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    public static String retryInvoke(String method, String function, String jsonData)
            throws IOException, GeneralSecurityException {
        LogUtil.printAndLogError(null, ApplicationMessageConstants.REQUEST_FAILED_AND_RETRY);
        HttpURLConnection connection = null;
        try {
                connection = createConnection(method, getAPIUrl() + function, ApplicationProxyUtil.getRetryProxy());
            String result = sendAndReceiveData(connection, jsonData);
            LogUtil.printOutputLine(ApplicationMessageConstants.REQUEST_COMPLETED);
            return result;
        } catch (IOException e) {
            LogUtil.logError(e);
            throw e;
        } catch (URISyntaxException e) {
            LogUtil.printAndLogError(null, ApplicationMessageConstants.REQUEST_FAILED);
            LogUtil.logError(e);
            return StringUtils.EMPTY;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    public static String getAPIUrl() {
        boolean isQTestVersion = Platform.getBundle(IdConstants.QTEST_INTEGRATION_BUNDLE_ID) != null;

        if (VersionUtil.isInternalBuild()) {
            if (isQTestVersion) {
                return DEVELOPMENT_QTEST_ACTIVATION_URL_API;
            }
            return DEVELOPMENT_URL_API;

        } else {
            if (isQTestVersion) {
                return PRODUCTION_QTEST_ACTIVATION_URL_API;
            }
            return PRODUCTION_URL_API;
        }
    }

    public static String getInformation(String url, JsonObject jsonObject) {
        try {
            return invoke(GET, url, jsonObject.toString());
        } catch (IOException | GeneralSecurityException ex) {
            return null;
        }
    }

    public static String getInformation(String url) {
        try {
            return invoke(GET, url, null);
        } catch (IOException | GeneralSecurityException ex) {
            return null;
        }
    }

    public static JsonObject getJsonInformation(String url, JsonObject jsonObject) {
        try {
            return new JsonParser().parse(invoke(GET, url, jsonObject.toString())).getAsJsonObject();
        } catch (IOException | GeneralSecurityException ex) {
            return null;
        }
    }

    public static JsonObject getJsonInformation(String url, String jsonData) {
        try {
            return new JsonParser().parse(invoke(GET, url, jsonData)).getAsJsonObject();
        } catch (IOException | GeneralSecurityException ex) {
            return null;
        }
    }

    public static JsonObject getJsonInformation(String url) {
        try {
            return new JsonParser().parse(invoke(GET, url, null)).getAsJsonObject();
        } catch (IOException | GeneralSecurityException ex) {
            return null;
        }
    }

    private static String sendAndReceiveData(HttpURLConnection uc, String sendingData) throws IOException {
        if (StringUtils.isNotEmpty(sendingData)) {
            try (DataOutputStream wr = new DataOutputStream(uc.getOutputStream())) {
                wr.writeBytes(sendingData);
            }
        }
        String result = "";
        int statusCode = uc.getResponseCode();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader((statusCode >= 400)? uc.getErrorStream(): uc.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            result = response.toString().trim();
        }

        return result;
    }

    public static HttpURLConnection createConnection(String method, String sUrl, Proxy proxy)
            throws IOException, GeneralSecurityException {
        URL url = new URL(sUrl);

        if (HTTP.equals(url.getProtocol())) {
            HttpURLConnection uc = null;
            uc = (HttpURLConnection) url.openConnection(proxy);
            uc.setRequestMethod(method);
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setUseCaches(false);
            uc.setDoOutput(true);
            return uc;
        } else {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, getTrustManagers(), new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection uc = null;
            uc = (HttpsURLConnection) url.openConnection(proxy);
            uc.setHostnameVerifier(getHostnameVerifier());
            uc.setRequestMethod(method);
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setUseCaches(false);
            uc.setDoOutput(true);
            return uc;
        }
    }

    private static TrustManager[] getTrustManagers() throws IOException {
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

    private static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
    }

}
