package com.kms.katalon.core.webservice.common;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.model.SSLClientCertificateSettings;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;

public class ServiceRequestFactory {

    public static Requestor getInstance(RequestObject request) {
        ProxyInformation proxyInfo = request.getProxyInformation() != null ? request.getProxyInformation()
                : RunConfiguration.getProxyInformation();
        return getInstance(request, RunConfiguration.getProjectDir(), proxyInfo, true);
    }

    public static Requestor getInstance(RequestObject request, String projectDir, 
    		ProxyInformation proxyInformation, boolean calledFromKeyword) {
    	// Overwrite useMobBrowserProxy if it's from WebServicePart/SoapServicePart
//    	if(!calledFromKeyword){
//    		proxyInformation.setDisableMobBrowserProxy(true);
//    	}
        if (RequestHeaderConstants.SOAP.equalsIgnoreCase(request.getServiceType())) {
            return new SoapClient(projectDir, proxyInformation);
        } else {
            return new RestfulClient(projectDir, proxyInformation);
        }
    }
}
