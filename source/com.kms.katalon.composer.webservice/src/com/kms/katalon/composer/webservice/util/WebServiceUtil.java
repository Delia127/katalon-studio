package com.kms.katalon.composer.webservice.util;

import static com.kms.katalon.entity.repository.WebServiceRequestEntity.DELETE_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.GET_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.POST_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.PUT_METHOD;

import com.kms.katalon.composer.webservice.constants.ImageConstants;

public class WebServiceUtil {

    public static String getRequestMethodIcon(String restRequestMethod) {
        switch (restRequestMethod) {
            case GET_METHOD:
                return ImageConstants.GET_METHOD_16;
            case POST_METHOD:
                return ImageConstants.POST_METHOD_16;
            case PUT_METHOD:
                return ImageConstants.PUT_METHOD_16;
            case DELETE_METHOD:
                return ImageConstants.DELETE_METHOD_16;
            default:
                return ImageConstants.WS_TEST_OBJECT_16;
        }
    }
}
