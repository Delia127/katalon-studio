package com.kms.katalon.core.webservice.common;

import com.kms.katalon.core.testobject.RequestObject;

public class ServiceRequestFactory {

	public static Requestor getInstance(RequestObject request){
		if("SOAP".equalsIgnoreCase(request.getServiceType())){
			return new SoapClient();
		}
		else{
			return new RestfulClient();
		}
	}
}
