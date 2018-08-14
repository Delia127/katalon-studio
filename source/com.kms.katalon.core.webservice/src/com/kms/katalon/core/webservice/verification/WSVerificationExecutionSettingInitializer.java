package com.kms.katalon.core.webservice.verification;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.testobject.HttpBodyContent;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent;

public class WSVerificationExecutionSettingInitializer {

    public static void initSettingForWebServiceRequestVerification() throws Exception {

        String requestObjectId = (String) RunConfiguration
                .getProperty(StringConstants.WS_VERIFICATION_REQUEST_OBJECT_ID);

        String responseObjectJson = (String) RunConfiguration
                .getProperty(StringConstants.WS_VERIFICATION_RESPONSE_OBJECT);

        Gson gson = new GsonBuilder().registerTypeAdapter(HttpBodyContent.class, new HttpBodyContentInstanceCreator())
                .create();

        ResponseObject response = gson.fromJson(responseObjectJson, ResponseObject.class);
        HttpBodyContent textBodyContent = new HttpTextBodyContent(response.getResponseBodyContent());
        response.setBodyContent(textBodyContent);

        WSVerificationExecutionSetting.setRequestObjectId(requestObjectId);
        WSVerificationExecutionSetting.setResponseObject(response);
    }

    private static class HttpBodyContentInstanceCreator implements InstanceCreator<HttpBodyContent> {

        @Override
        public HttpBodyContent createInstance(Type arg0) {
            return new HttpTextBodyContent(StringUtils.EMPTY);
        }

    }

    public static void initSettingForSendRequestAndVerifyKeyword(String requestObjectId,
            ResponseObject responseObject) {

        WSVerificationExecutionSetting.setRequestObjectId(requestObjectId);
        WSVerificationExecutionSetting.setResponseObject(responseObject);
    }
}
