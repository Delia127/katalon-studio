package com.kms.katalon.core.webservice.common;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;

public class ServiceRequestFactory {

    public static Requestor getInstance(RequestObject request) {
        return getInstance(request, RunConfiguration.getProjectDir(), RunConfiguration.getProxyInformation());
    }

    public static Requestor getInstance(RequestObject request, String projectDir, ProxyInformation proxyInformation) {
        if (RequestHeaderConstants.SOAP.equalsIgnoreCase(request.getServiceType())) {
            return new SoapClient(projectDir, proxyInformation);
        } else {
            return new RestfulClient(projectDir, proxyInformation);
        }
    }
}
