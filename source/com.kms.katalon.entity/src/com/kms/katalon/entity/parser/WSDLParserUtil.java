package com.kms.katalon.entity.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kms.katalon.composer.webservice.util.WSDLHelper;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WSDLParserUtil {
	@SuppressWarnings({ "static-access", "finally" })
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(String requestMethod, String url) throws Exception{
		List<WebServiceRequestEntity> newWSTestObjects = new ArrayList<WebServiceRequestEntity>();
		
		try{
			
			WSDLHelper wsdlHelperInstance = WSDLHelper.newInstance(url, null);
			Map<String, List<String>> paramMap = wsdlHelperInstance.getParamMap();

			for(Object objOperationName: SafeUtils.safeList(wsdlHelperInstance.getOperationNamesByRequestMethod(requestMethod))){
				if(objOperationName != null){
					String operationName = (String) objOperationName;
					WebServiceRequestEntity newWSREntity = new WebServiceRequestEntity();
					newWSREntity.setWsdlAddress(url);
					newWSREntity.setName(operationName);
					newWSREntity.setSoapRequestMethod(requestMethod);
					newWSREntity.setSoapServiceFunction(operationName);
					
					String SOAPBodyMessage = wsdlHelperInstance.generateInputSOAPMessageText(url, null, requestMethod, operationName, paramMap);
					if(SOAPBodyMessage != null){
						newWSREntity.setSoapBody(SOAPBodyMessage);
					}
					newWSTestObjects.add(newWSREntity);
				}
			}
			
		} catch (Exception ex) {
			throw ex;
	    } finally {
	    	if(newWSTestObjects.size() > 0 ) { 	    		
	    		return newWSTestObjects;
	    	} else 
	    		return null;
	    }
	}
}
