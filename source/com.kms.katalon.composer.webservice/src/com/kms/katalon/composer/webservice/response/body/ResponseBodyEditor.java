package com.kms.katalon.composer.webservice.response.body;

import com.kms.katalon.core.testobject.ResponseObject;

public interface ResponseBodyEditor {
    
    public void switchModeContentBody(ResponseObject responseObject);
    
    public void updateContentBody(ResponseObject responseObject);
    
    public String getContentType();
    
}
