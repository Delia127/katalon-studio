package com.kms.katalon.composer.webservice.response.body;

import java.io.IOException;

import com.kms.katalon.core.testobject.ResponseObject;

public interface ResponseBodyEditor {

    public void switchModeContentBody(ResponseObject responseObject) throws IOException;

    public void setContentBody(ResponseObject responseObject) throws IOException;

    public String getContentType();

}
