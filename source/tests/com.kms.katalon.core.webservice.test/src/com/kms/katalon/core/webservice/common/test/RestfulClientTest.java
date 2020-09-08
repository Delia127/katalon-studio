package com.kms.katalon.core.webservice.common.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.FormDataBodyParameter;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.RestRequestObjectBuilder;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.testobject.UrlEncodedBodyParameter;
import com.kms.katalon.core.webservice.common.RestfulClient;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

public class RestfulClientTest {

    private static final String SAMPLE_WEB_SERVICE_AUT = "sample-web-service-aut.herokuapp.com";

    private SSLClientCertificateSettings getDefaultClientCertificateSettings() {
        SSLClientCertificateSettings settings = new SSLClientCertificateSettings();
        settings.setKeyStoreFile(StringUtils.EMPTY);
        settings.setKeyStorePassword(StringUtils.EMPTY);
        return settings;
    }

    private RestfulClient getClient() throws Exception {
        WebServiceSettingStore store = WebServiceSettingStore.create("");
        WebServiceSettingStore spiedStore = Mockito.spy(store);
        doReturn(SSLCertificateOption.BYPASS).when(spiedStore).getSSLCertificateOption();

        SSLClientCertificateSettings clientCertificateSettings = getDefaultClientCertificateSettings();
        doReturn(clientCertificateSettings).when(spiedStore).getClientCertificateSettings();

        RestfulClient restClient = new RestfulClient("", null);
        restClient.setSettingStore(spiedStore);
        return restClient;
    }

    @Test
    public void sendRequestTestAcceptJSON() throws Exception {
        RestfulClient client = getClient();
        RequestObject request = new RequestObject("Test Request");
        request.setRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/accept-json");
        request.setRestRequestMethod("GET");

        ResponseObject response = client.send(request);

        assertEquals(response.getStatusCode(), 200);
        assertTrue("Response JSON", response.isJsonContentType());
        assertEquals(response.getResponseBodyContent().contains("John Smith"), true);
        assertEquals(response.getResponseBodyContent().contains("Maria Rodriguez"), true);
        assertEquals(response.getResponseBodyContent().contains("James Johnson"), true);
    }

    @Test
    public void sendRequestTestAcceptXMLWithParam() throws Exception {
        RestfulClient client = getClient();
        RequestObject request = new RequestObject("Test Request 2");
        request.setRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/accept-xml");
        request.setRestRequestMethod("GET");
        List<TestObjectProperty> params = new ArrayList<>();
        params.add(new TestObjectProperty("gender", null, "MALE"));
        request.setRestParameters(params);

        ResponseObject response = client.send(request);

        // assertEquals(response.getContentType().contains("FEMALE"), true);
        assertEquals(response.getStatusCode(), 200);
        assertTrue(response.isXmlContentType());
        assertNotNull(response.getResponseBodyContent());
        assertEquals(response.getResponseBodyContent().contains("FEMALE"), false);
    }

    @Test
    public void sendRequestTestPostWithFormData() throws Exception {
        RestfulClient client = getClient();
        RestRequestObjectBuilder builder = new RestRequestObjectBuilder();
        List<FormDataBodyParameter> params = new ArrayList<>();
        params.add(new FormDataBodyParameter("username", "Owen", FormDataBodyParameter.PARAM_TYPE_TEXT));
        params.add(new FormDataBodyParameter("password", "123", FormDataBodyParameter.PARAM_TYPE_TEXT));
        params.add(new FormDataBodyParameter("gender", "MALE", FormDataBodyParameter.PARAM_TYPE_TEXT));
        params.add(new FormDataBodyParameter("age", "11", FormDataBodyParameter.PARAM_TYPE_TEXT));
        Path directory = Paths.get("resources", "mucus.jpg");
        String avatar = directory.toFile().getAbsolutePath();
        params.add(new FormDataBodyParameter("avatar", avatar, FormDataBodyParameter.PARAM_TYPE_FILE));
        List<TestObjectProperty> headers = new ArrayList<>();
        headers.add(new TestObjectProperty("Content-Type", ConditionType.EQUALS, "multipart/form-data"));
        RequestObject request = builder.withRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/form-data")
                .withRestRequestMethod("POST").withHttpHeaders(headers).withMultipartFormDataBodyContent(params)
                .build();

        ResponseObject response = client.send(request);

        assertEquals(response.getStatusCode(), 200);
        assertTrue(response.isJsonContentType());
        assertNotNull(response.getResponseBodyContent());
        assertEquals(response.getResponseBodyContent().contains("Owen"), true);
        assertEquals(response.getResponseBodyContent().contains("123"), true);
        assertEquals(response.getResponseBodyContent().contains("MALE"), true);
        assertEquals(response.getResponseBodyContent().contains("11"), true);

    }

    @Test
    public void sendRequestTestPostWithURLEncoded() throws Exception {
        RestfulClient client = getClient();
        RestRequestObjectBuilder builder = new RestRequestObjectBuilder();
        List<UrlEncodedBodyParameter> params = new ArrayList<>();
        params.add(new UrlEncodedBodyParameter("username", "Adam"));
        params.add(new UrlEncodedBodyParameter("password", "111"));
        params.add(new UrlEncodedBodyParameter("gender", "MALE"));
        params.add(new UrlEncodedBodyParameter("age", "14"));
        params.add(new UrlEncodedBodyParameter("avatar", null));
        List<TestObjectProperty> headers = new ArrayList<>();
        headers.add(new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded"));
        RequestObject request = builder.withRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/urlencoded")
                .withRestRequestMethod("POST").withHttpHeaders(headers).withUrlEncodedBodyContent(params).build();

        ResponseObject response = client.send(request);

        assertEquals(response.getStatusCode(), 200);
        assertTrue(response.isJsonContentType());
        assertNotNull(response.getResponseBodyContent());
        assertEquals(response.getResponseBodyContent().contains("Adam"), true);
        assertEquals(response.getResponseBodyContent().contains("111"), true);
        assertEquals(response.getResponseBodyContent().contains("MALE"), true);
        assertEquals(response.getResponseBodyContent().contains("14"), true);

    }

    @Test
    public void sendRequestTestPostWithFile() throws Exception {
        RestfulClient client = getClient();
        RestRequestObjectBuilder builder = new RestRequestObjectBuilder();
        List<TestObjectProperty> headers = new ArrayList<>();
        headers.add(new TestObjectProperty("Content-Type", ConditionType.EQUALS, "image/png"));
        Path directory = Paths.get("resources", "mucus.jpg");
        String filepath = directory.toFile().getAbsolutePath();
        RequestObject request = builder.withRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/upload")
                .withRestRequestMethod("POST").withHttpHeaders(headers).withFileBodyContent(filepath).build();

        ResponseObject response = client.send(request);

        assertEquals(response.getStatusCode(), 200);
        assertTrue(response.isJsonContentType());
        assertNotNull(response.getResponseBodyContent());
        assertEquals(response.getResponseBodyContent().contains("fileSize"), true);
        assertEquals(response.getResponseBodyContent().contains("image/png"), true);
    }

    /*
     * @Test public void sendRequestTestPostWithXML() throws Exception{
     * RestfulClient client = getClient(); RequestObject request = new
     * RequestObject("Test Request 5"); RestRequestObjectBuilder builder = new
     * RestRequestObjectBuilder(); List<UrlEncodedBodyParameter> params = new
     * ArrayList<>(); params.add(new UrlEncodedBodyParameter("username",
     * "Iron")); params.add(new UrlEncodedBodyParameter("password", "zxc"));
     * params.add(new UrlEncodedBodyParameter("gender", "MALE")); params.add(new
     * UrlEncodedBodyParameter("age", "20")); params.add(new
     * UrlEncodedBodyParameter("avatar", null)); List<TestObjectProperty>
     * headers = new ArrayList<>(); headers.add(new
     * TestObjectProperty("Content-Type", ConditionType.EQUALS,
     * "application/xml")); request =
     * builder.withRestUrl("http://localhost:8080/api/users/xml")
     * .withRestRequestMethod("POST") .withHttpHeaders(headers)
     * .withUrlEncodedBodyContent(params).build();
     * 
     * ResponseObject response = client.send(request);
     * 
     * assertEquals(response.getStatusCode(), 200);
     * assertTrue(response.isJsonContentType());
     * assertNotNull(response.getResponseBodyContent());
     * assertEquals(response.getResponseBodyContent().contains("Adam"), true);
     * assertEquals(response.getResponseBodyContent().contains("123"), true);
     * assertEquals(response.getResponseBodyContent().contains("MALE"), true);
     * assertEquals(response.getResponseBodyContent().contains("11"), true);
     * 
     * }
     */

    @Test
    public void sendRequestTestGetById() throws Exception {
        RestfulClient client = getClient();
        RequestObject request = new RequestObject("Test Request 7");
        request.setRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/1");
        request.setRestRequestMethod("GET");

        ResponseObject response = client.send(request);

        assertEquals(response.getStatusCode(), 200);
        assertTrue(response.isJsonContentType());
        assertNotNull(response.getResponseBodyContent());
        assertEquals(response.getResponseBodyContent().contains("\"id\":1"), true);
        assertEquals(response.getResponseBodyContent().contains("John Smith"), true);
        assertEquals(response.getResponseBodyContent().contains("password"), true);
        assertEquals(response.getResponseBodyContent().contains("gender"), true);
        assertEquals(response.getResponseBodyContent().contains("age"), true);
    }

    @Test
    public void sendRequestTestUpdateAge() throws Exception {
        RestfulClient client = getClient();
        List<TestObjectProperty> headers = new ArrayList<>();
        headers.add(new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded"));
        List<UrlEncodedBodyParameter> params = new ArrayList<>();
        params.add(new UrlEncodedBodyParameter("age", "33"));

        RestRequestObjectBuilder builder = new RestRequestObjectBuilder();
        RequestObject request = builder.withRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/2")
                .withRestRequestMethod("PUT").withHttpHeaders(headers).withUrlEncodedBodyContent(params).build();

        ResponseObject response = client.send(request);

        assertEquals(response.getStatusCode(), 200);
        assertTrue(response.isJsonContentType());
        assertNotNull(response.getResponseBodyContent());
        assertEquals(response.getResponseBodyContent().contains("\"age\":33"), true);
        assertEquals(response.getResponseBodyContent().contains("\"id\":2"), true);
    }

}
