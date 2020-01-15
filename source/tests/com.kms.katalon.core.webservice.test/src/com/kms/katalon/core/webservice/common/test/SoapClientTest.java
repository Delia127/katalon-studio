package com.kms.katalon.core.webservice.common.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.webservice.common.SoapClient;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

public class SoapClientTest {

    @Test
    public void testSendRequest() throws Exception {
        SoapClient soapClient = getSoapClient();
        RequestObject request = getRequestObject();
        ResponseObject response = soapClient.send(request);

        assertThat("Request should be sent successfully", response.getStatusCode() == 200);

        String responseBody = response.getResponseText();
        assertTrue(StringUtils.isNotBlank(responseBody));
        assertNotNull(response.getHeaderField("Content-Type"));
        assertTrue(response.getHeaderField("Content-Type").contains("/xml"));
    }

    private RequestObject getRequestObject() throws IOException {
        RequestObject request = new RequestObject("Test Request");
        request.setWsdlAddress("http://www.dataaccess.com/webservicesserver/numberconversion.wso?WSDL");
        request.setSoapRequestMethod("SOAP");
        request.setSoapServiceFunction("NumberToDollars");
        InputStream requestBodyInputStream = this.getClass().getClassLoader()
                .getResourceAsStream("resources/numberconverter-service/number-to-dollars-request.xml");
        request.setSoapBody(IOUtils.toString(requestBodyInputStream));
        return request;
    }

    private SoapClient getSoapClient() throws IOException {
        WebServiceSettingStore store = WebServiceSettingStore.create("");
        WebServiceSettingStore spiedStore = Mockito.spy(store);
        doReturn(SSLCertificateOption.BYPASS).when(spiedStore).getSSLCertificateOption();

        SSLClientCertificateSettings clientCertificateSettings = getDefaultClientCertificateSettings();
        doReturn(clientCertificateSettings).when(spiedStore).getClientCertificateSettings();

        SoapClient soapClient = new SoapClient("", null);
        soapClient.setSettingStore(spiedStore);
        return soapClient;
    }

    private SSLClientCertificateSettings getDefaultClientCertificateSettings() {
        SSLClientCertificateSettings settings = new SSLClientCertificateSettings();
        settings.setKeyStoreFile(StringUtils.EMPTY);
        settings.setKeyStorePassword(StringUtils.EMPTY);
        return settings;
    }
}
