package com.kms.katalon.core.webservice.definition;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.webservice.common.HttpUtil;
import com.kms.katalon.core.webservice.constants.DefinitionLoaderParams;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;

public class DefinitionApiLoader extends AbstractDefinitionLoader {

    private Map<String, String> httpHeaders;

    private ProxyInformation proxyInformation;

    private SSLCertificateOption certificateOption;

    private SSLClientCertificateSettings clientCertSettings;

    @SuppressWarnings("unchecked")
    public DefinitionApiLoader(String location, Map<String, Object> params) {
        this.definitionLocation = location;
        this.httpHeaders = (Map<String, String>) params.get(DefinitionLoaderParams.HTTP_HEADERS);
        this.proxyInformation = (ProxyInformation) params.get(DefinitionLoaderParams.PROXY);
        this.certificateOption = (SSLCertificateOption) params.get(DefinitionLoaderParams.CERT_OPTION);
        this.clientCertSettings = (SSLClientCertificateSettings) params.get(DefinitionLoaderParams.SSL_CLIENT_CERT);
    }

    @Override
    public InputStream load() {
        try {
            HttpGet get = buildGetRequest(getDefinitionLocation());
            HttpResponse response = HttpUtil.sendRequest(
                get,
                true,
                proxyInformation,
                certificateOption,
                clientCertSettings);
            HttpEntity responseEntity = response.getEntity();
            InputStream is = null;
            if (responseEntity != null) {
                is = responseEntity.getContent();
            }
            return is;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpGet buildGetRequest(String url) {
        HttpGet get = new HttpGet(url);
        setHttpConnectionHeaders(get);
        return get;
    }

    private void setHttpConnectionHeaders(HttpGet get) {
        if (httpHeaders != null) {
            httpHeaders.entrySet().stream().forEach(e -> get.addHeader(e.getKey(), e.getValue()));
        }
    }
}
