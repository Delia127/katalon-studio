package com.kms.katalon.core.webservice.wsdl.support.wsdl;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.webservice.common.HttpUtil;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.constants.WsdlLocatorParams;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;

@SuppressWarnings("unchecked")
public class WsdlDefinitionApiLocator extends BaseWsdlDefinitionLocator {

    private Map<String, String> httpHeaders;

    private ProxyInformation proxyInformation;

    private SSLCertificateOption certificateOption;

    private SSLClientCertificateSettings clientCertSettings;
    
    public WsdlDefinitionApiLocator(String wsdlLocation, Map<String, Object> params) {

        this.wsdlLocation = wsdlLocation;
        this.httpHeaders = (Map<String, String>) params.get(WsdlLocatorParams.HTTP_HEADERS);
        this.proxyInformation = (ProxyInformation) params.get(WsdlLocatorParams.PROXY);
        this.certificateOption = (SSLCertificateOption) params.get(WsdlLocatorParams.CERT_OPTION);
        this.clientCertSettings = (SSLClientCertificateSettings) params.get(WsdlLocatorParams.SSL_CLIENT_CERT);
    }

    @Override
    protected boolean isAbsoluteUrl(String url) {
        url = url.toLowerCase();
        return url.startsWith(RequestHeaderConstants.HTTP) || url.startsWith(RequestHeaderConstants.HTTPS);
    }

    @Override
    protected InputStream load(String url) {
        try {
            HttpGet get = buildGetRequest(url);

            HttpResponse response = HttpUtil.sendRequest(
                    get, true,
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

    @Override
    public void close() {
    }
}
