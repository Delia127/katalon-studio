package com.kms.katalon.core.webservice.common.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.RestRequestObjectBuilder;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.webservice.common.RestfulClient;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;

import groovy.json.JsonBuilder;

public class RestfulClientTest {
	private static final String SAMPLE_WEB_SERVICE_AUT = "sample-web-service-aut.herokuapp.com";
	private static final String SAMPLE_WEB_SERVICE_AUT_LOCAL = "localhost:8080";

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
    
    private Map<String, Object> createJsonUser(){
    	Map<String, Object> map = new LinkedHashMap<>();
    	map.put("id", Long.valueOf("1"));
    	map.put("username", "John Smith");
    	map.put("password", "123");
    	map.put("gender", "MALE");
    	map.put("age", 25);
    	map.put("avatar", null);
    	return map;
    }
    
    @Test
    public void sendRequestTestGetWithBody() throws Exception{
    	Map<String, Object> jsonUser = createJsonUser();
    	
    	List<TestObjectProperty> headers = new ArrayList<>();
    	headers.add(new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"));
    	
    	RestfulClient client = getClient();
    	RequestObject request = new RequestObject("Test Request");
    	RestRequestObjectBuilder builder = new RestRequestObjectBuilder();  	
    	JsonBuilder jsonBuilder = new JsonBuilder();
    	
    	String bodyContent = jsonBuilder.call(jsonUser).toString();
    	bodyContent = jsonBuilder.toPrettyString();
    	request = builder.withTextBodyContent(bodyContent)
    					 .withHttpHeaders(headers)
    					 .withRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/json")
    					 .withRestRequestMethod("GET")
    					 .build();
   
    	ResponseObject response = client.send(request);
    	
    	assertEquals(response.getStatusCode(), 200);
    	assertNotNull(response.getResponseBodyContent());
    	assertEquals(response.getResponseBodyContent().contains("\"id\":1"), true);
        assertEquals(response.getResponseBodyContent().contains("username"), true);
        assertEquals(response.getResponseBodyContent().contains("password"), true);
        assertEquals(response.getResponseBodyContent().contains("gender"), true);
        assertEquals(response.getResponseBodyContent().contains("age"), true);
    	
    }
    
    @Test
    public void sendRequestTestPostWithoutBody() throws Exception{
    	RestfulClient client = getClient();
    	RequestObject request = new RequestObject("Test Request");
    	RestRequestObjectBuilder builder = new RestRequestObjectBuilder();
    	request = builder.withRestUrl("http://" + SAMPLE_WEB_SERVICE_AUT + "/api/users/json/no-body")
    					 .withRestRequestMethod("POST").build();
   
    	ResponseObject response = client.send(request);
    	
    	assertEquals(response.getStatusCode(), 200);
    	assertNotNull(response.getResponseBodyContent());
    	assertEquals(response.getResponseBodyContent().contains(""), true);
        assertEquals(response.getResponseBodyContent().contains("username"), true);
        assertEquals(response.getResponseBodyContent().contains("password"), true);
        assertEquals(response.getResponseBodyContent().contains("UNKNOWN"), true);
        assertEquals(response.getResponseBodyContent().contains("0"), true);
    	
    }
}
 