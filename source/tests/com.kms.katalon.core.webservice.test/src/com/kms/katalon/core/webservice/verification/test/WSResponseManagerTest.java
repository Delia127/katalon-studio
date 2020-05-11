package com.kms.katalon.core.webservice.verification.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.webservice.verification.WSResponseManager;

public class WSResponseManagerTest {

    private static final String expectedResponseText = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Pet><category><id>1</id><name>string</name></category><id>1</id><name>SEBEK</name><photoUrls><photoUrl>string</photoUrl></photoUrls><status>available</status><tags><tag><id>1</id><name>string</name></tag></tags></Pet>";

    private static final String expectedResponseBodyContent = "<id>1</id>";

    private static String responseTextJson = "";

    private static final Map<String, Object> mapObject = new HashMap<>();

    @Before
    public void inintValue() throws IOException {

        InputStream is = this.getClass()
                .getClassLoader()
                .getResourceAsStream("resources/responsejson-file/response.json");
        responseTextJson = getStringFromInputStream(is);

        mapObject.put(StringConstants.WS_VERIFICATION_RESPONSE_OBJECT, responseTextJson);
        RunConfiguration.setExecutionSetting(mapObject);
    }

    @Test
    public void testGetCurrentResponseObjectText() throws Exception {
        
        ResponseObject response = WSResponseManager.getInstance().getCurrentResponse();
        
        //response text and response body content cannot be null
        Assert.assertEquals(expectedResponseText, response.getResponseText());
        Assert.assertEquals(expectedResponseBodyContent, response.getResponseBodyContent());
    }

    
    private String getStringFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }

        return resultStringBuilder.toString();
    }
}
