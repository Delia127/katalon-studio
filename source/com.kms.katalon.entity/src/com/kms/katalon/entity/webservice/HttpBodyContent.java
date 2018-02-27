package com.kms.katalon.entity.webservice;

public interface HttpBodyContent {

    String getContentType();
    
    long getContentLength();
    
    String getCharset();
}
