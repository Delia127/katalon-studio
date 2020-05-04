package com.kms.katalon.core.webservice.helper;

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
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.constants.WsdlLocatorParams;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlDefinitionApiLocator;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlDefinitionFileLocator;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlDefinitionLocator;

@SuppressWarnings("unchecked")
public class WsdlLocatorProvider {

    public static WsdlDefinitionLocator getLocator(String wsdlLocation) {
        try {
            return getLocator(wsdlLocation, Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static WsdlDefinitionLocator getLocator(String wsdlLocation, Map<String, Object> params) throws IOException {
        if (StringUtils.isBlank(wsdlLocation)) {
            throw new IllegalArgumentException("WSDL location must not be null or empty.");
        }
        
        if (isWebUrl(wsdlLocation)) {
            return getLocatorForApiEndpoint(wsdlLocation, params);
        }
        return getLocatorForFileLocation(wsdlLocation, params);

    }

    private static WsdlDefinitionLocator getLocatorForFileLocation(String wsdlLocation, Map<String, Object> params) {
        return new WsdlDefinitionFileLocator(wsdlLocation, Collections.emptyMap());
    }

    private static WsdlDefinitionLocator getLocatorForApiEndpoint(String wsdlLocation, Map<String, Object> params)
            throws IOException {

        String projectLocation = getProjectLocation();
        if (StringUtils.isBlank(projectLocation)) {
            return null;
        }

        Map<String, Object> inputParams = params != null ? params : Collections.emptyMap();

        Map<String, Object> locatorParams = populateLocatorParams(projectLocation, inputParams);

        return new WsdlDefinitionApiLocator(wsdlLocation, locatorParams);
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

    private static Map<String, Object> populateLocatorParams(String projectLocation, Map<String, Object> params)
            throws IOException {
        Map<String, Object> locatorParams = new HashMap<>();

        WebServiceSettingStore settingStore = WebServiceSettingStore.create(projectLocation);

        SSLCertificateOption certOption = getSSLCertificateOption(settingStore, params);
        locatorParams.put(WsdlLocatorParams.CERT_OPTION, certOption);

        SSLClientCertificateSettings clientCertSettings = getSSLClientCertificateSettings(settingStore, params);
        locatorParams.put(WsdlLocatorParams.SSL_CLIENT_CERT, clientCertSettings);

        Map<String, String> headers = getHttpHeaders(params);
        locatorParams.put(WsdlLocatorParams.HTTP_HEADERS, headers);

        ProxyInformation proxyInformation = getProxyInformation(params);
        locatorParams.put(WsdlLocatorParams.PROXY, proxyInformation);

        return locatorParams;

    }

    private static SSLCertificateOption getSSLCertificateOption(WebServiceSettingStore settingStore,
            Map<String, Object> params) throws IOException {

        SSLCertificateOption certOption = (SSLCertificateOption) params.get(WsdlLocatorParams.CERT_OPTION);
        if (certOption == null) {
            certOption = settingStore.getSSLCertificateOption();
        }
        return certOption;
    }

    private static SSLClientCertificateSettings getSSLClientCertificateSettings(WebServiceSettingStore settingStore,
            Map<String, Object> params) throws IOException {

        SSLClientCertificateSettings clientCertSettings = (SSLClientCertificateSettings) params
                .get(WsdlLocatorParams.SSL_CLIENT_CERT);
        if (clientCertSettings == null) {
            clientCertSettings = settingStore.getClientCertificateSettings();
        }
        return clientCertSettings;
    }

    private static Map<String, String> getHttpHeaders(Map<String, Object> params) {
        Map<String, String> headers = (Map<String, String>) params.get(WsdlLocatorParams.HTTP_HEADERS);
        if (headers == null) {
            headers = Collections.emptyMap();
        }
        return headers;
    }

    private static ProxyInformation getProxyInformation(Map<String, Object> params) {
        ProxyInformation proxyInformation = (ProxyInformation) params.get(WsdlLocatorParams.PROXY);
        if (proxyInformation == null) {
            try {
                proxyInformation = RunConfiguration.getProxyInformation();
            } catch (Exception ignored) {
            }

            if (proxyInformation == null) {
                String proxyJson = System.getProperty(SystemProperties.PROXY);
                proxyInformation = JsonUtil.fromJson(proxyJson, ProxyInformation.class);
            }
        }

        return proxyInformation;
    }

    private static boolean isWebUrl(String url) {
        return url.startsWith(RequestHeaderConstants.HTTP) || url.startsWith(RequestHeaderConstants.HTTPS);
    }
}
