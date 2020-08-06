package com.kms.katalon.composer.webservice.soapui;

public class SoapUIAttachment {

    private String name;
    
    private String contentType;
    
    private String contentId;
    
    private String url;

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getContentType() {
        return contentType;
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected String getContentId() {
        return contentId;
    }

    protected void setContentId(String contentId) {
        this.contentId = contentId;
    }

    protected String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        this.url = url;
    }
}
