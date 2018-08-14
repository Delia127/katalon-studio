package com.kms.katalon.core.webservice.verification;

import java.io.File;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;

public class WSResponseManager {

    private static WSResponseManager instance;

    private WSResponseManager() {
        
    }
    
    public static WSResponseManager getInstance() {
        if (instance == null) {
            instance = new WSResponseManager();
        }
        return instance;
    }
    
    public RequestObject getCurrentRequest() throws Exception {
        String requestObjectId = WSVerificationExecutionSetting.getRequestObjectId();
        File objectFile = new File(RunConfiguration.getProjectDir(), requestObjectId + ".rs");
        RequestObject requestObject = (RequestObject) ObjectRepository.findRequestObject(
                requestObjectId, objectFile);
        return requestObject;
    }
   
    
    public ResponseObject getCurrentResponse() throws Exception {
        return WSVerificationExecutionSetting.getResponseObject();
    }
}
