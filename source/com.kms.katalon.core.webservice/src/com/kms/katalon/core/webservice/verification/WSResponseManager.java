package com.kms.katalon.core.webservice.verification;

import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.testobject.HttpBodyContent;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent;

public class WSResponseManager {
    
    public static ResponseObject getCurrentResponse() throws Exception {
        String responseObjectJson = (String) RunConfiguration.getProperty(StringConstants.WS_RESPONSE_OBJECT);
        
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(HttpBodyContent.class, new HttpBodyContentInstanceCreator())
                .create();
        
        ResponseObject responseObject = gson.fromJson(responseObjectJson, ResponseObject.class);
        HttpBodyContent textBodyContent = new HttpTextBodyContent(responseObject.getResponseBodyContent());
        responseObject.setBodyContent(textBodyContent);
        
        return responseObject;
    }
    
    private static class HttpBodyContentInstanceCreator implements InstanceCreator<HttpBodyContent> {

        @Override
        public HttpBodyContent createInstance(Type arg0) {
            return new HttpTextBodyContent(StringUtils.EMPTY);
        }
        
    }
}
