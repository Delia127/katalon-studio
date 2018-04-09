package com.kms.katalon.composer.webservice.util;

import static com.kms.katalon.entity.repository.WebServiceRequestEntity.DELETE_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.GET_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.POST_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.PUT_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.SOAP;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.SOAP12;

import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebServiceUtil {

    public static String getRequestMethodIcon(String serviceType, String requestMethod) {
        if (serviceType.equals(WebServiceRequestEntity.SERVICE_TYPES[1])) {
            switch (requestMethod) {
                case GET_METHOD:
                    return ImageConstants.WS_GET_METHOD_16;
                case POST_METHOD:
                    return ImageConstants.WS_POST_METHOD_16;
                case PUT_METHOD:
                    return ImageConstants.WS_PUT_METHOD_16;
                case DELETE_METHOD:
                    return ImageConstants.WS_DELETE_METHOD_16;
                // New rest web service request.
                default:
                    return ImageConstants.WS_GET_METHOD_16;
            }
        } 
        if (serviceType.equals(WebServiceRequestEntity.SERVICE_TYPES[0])) {
            switch (requestMethod) {
                case GET_METHOD:
                    return ImageConstants.WS_SOAP_GET_METHOD_16;
                case POST_METHOD:
                    return ImageConstants.WS_SOAP_POST_METHOD_16;
                case SOAP:
                    return ImageConstants.WS_SOAP_METHOD_16;
                case SOAP12:
                    return ImageConstants.WS_SOAP12_METHOD_16;
                // New rest web service request.
                default:
                    return ImageConstants.WS_GET_METHOD_16;
            }
        }
        return ImageConstants.WS_TEST_OBJECT_16;
    }
}
