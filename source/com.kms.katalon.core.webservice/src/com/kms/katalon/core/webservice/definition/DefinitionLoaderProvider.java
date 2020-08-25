package com.kms.katalon.core.webservice.definition;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.constants.SystemProperties;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webservice.constants.DefinitionLoaderParams;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

public class DefinitionLoaderProvider {

    public static DefinitionLoader getLoader(String location) {
        return getLoader(location, Collections.emptyMap());
    }

    public static DefinitionLoader getLoader(String location, Map<String, Object> params) {
        try {
            if (StringUtils.isBlank(location)) {
                throw new IllegalArgumentException("Location must not be null or empty.");
            }

            if (isWebUrl(location)) {
                return getLoaderForWebLocation(location, params);
            }
            return getLoaderForFileLocation(location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static DefinitionLoader getLoaderForFileLocation(String location) {
        return new DefinitionFileLoader(location);
    }

    private static DefinitionLoader getLoaderForWebLocation(String location, Map<String, Object> params)
            throws IOException {
        Map<String, Object> inputParams = params != null ? params : Collections.emptyMap();
        Map<String, Object> loaderParams = populateLoaderParams(inputParams);
        return new DefinitionApiLoader(location, loaderParams);
    }

    private static String getProjectLocation() {
        String projectLocation = null;
        try {
            projectLocation = RunConfiguration.getProjectDir();
        } catch (Exception ignored) {
        }
        if (StringUtils.isBlank(projectLocation) || "null".equals(projectLocation)) {
            projectLocation = System.getProperty(SystemProperties.PROJECT_LOCATION);
        }

        return projectLocation;
    }

    private static Map<String, Object> populateLoaderParams(Map<String, Object> params) throws IOException {
        Map<String, Object> locatorParams = new HashMap<>();

        String projectLocation = getProjectLocation();
        if (StringUtils.isBlank(projectLocation)) {
            return null;
        }

        WebServiceSettingStore settingStore = WebServiceSettingStore.create(projectLocation);

        SSLCertificateOption certOption = getSSLCertificateOption(settingStore, params);
        locatorParams.put(DefinitionLoaderParams.CERT_OPTION, certOption);

        SSLClientCertificateSettings clientCertSettings = getSSLClientCertificateSettings(settingStore, params);
        locatorParams.put(DefinitionLoaderParams.SSL_CLIENT_CERT, clientCertSettings);

        Map<String, String> headers = getHttpHeaders(params);
        locatorParams.put(DefinitionLoaderParams.HTTP_HEADERS, headers);

        ProxyInformation proxyInformation = getProxyInformation(params);
        locatorParams.put(DefinitionLoaderParams.PROXY, proxyInformation);

        return locatorParams;

    }

    private static SSLCertificateOption getSSLCertificateOption(WebServiceSettingStore settingStore,
            Map<String, Object> params) throws IOException {

        SSLCertificateOption certOption = (SSLCertificateOption) params.get(DefinitionLoaderParams.CERT_OPTION);
        if (certOption == null) {
            certOption = settingStore.getSSLCertificateOption();
        }
        return certOption;
    }

    private static SSLClientCertificateSettings getSSLClientCertificateSettings(WebServiceSettingStore settingStore,
            Map<String, Object> params) throws IOException {

        SSLClientCertificateSettings clientCertSettings = (SSLClientCertificateSettings) params
                .get(DefinitionLoaderParams.SSL_CLIENT_CERT);
        if (clientCertSettings == null) {
            clientCertSettings = settingStore.getClientCertificateSettings();
        }
        return clientCertSettings;
    }

    private static Map<String, String> getHttpHeaders(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        Map<String, String> headers = (Map<String, String>) params.get(DefinitionLoaderParams.HTTP_HEADERS);
        if (headers == null) {
            headers = Collections.emptyMap();
        }
        return headers;
    }

    private static ProxyInformation getProxyInformation(Map<String, Object> params) {
        ProxyInformation proxyInformation = (ProxyInformation) params.get(DefinitionLoaderParams.PROXY);
        if (proxyInformation == null) {
            try {
                proxyInformation = RunConfiguration.getProxyInformation();
            } catch (Exception ignored) {
            }

            if (proxyInformation == null) {
                String proxyJson = System.getProperty(SystemProperties.SYSTEM_PROXY);
                proxyInformation = JsonUtil.fromJson(proxyJson, ProxyInformation.class);
            }
        }

        return proxyInformation;
    }

    private static boolean isWebUrl(String url) {
        return url.startsWith(RequestHeaderConstants.HTTP) || url.startsWith(RequestHeaderConstants.HTTPS);
    }
}
