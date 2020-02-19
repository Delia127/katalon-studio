package com.kms.katalon.composer.webservice.viewmodel;

public class HttpBodyEditorViewModel {

    private String contentData;

    private String contentType;

    private String text;

    private boolean contentTypeUpdated = false;

    private boolean userAllowsAutoUpdateContentType = true;

    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    public boolean isContentTypeUpdated() {
        return contentTypeUpdated;
    }

    public void setContentTypeUpdated(boolean contentTypeUpdated) {
        this.contentTypeUpdated = contentTypeUpdated;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String type) {
        this.contentType = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserAllowsAutoUpdateContentType(boolean val) {
        this.userAllowsAutoUpdateContentType = val;
    }

    public boolean doesUserAllowAutoUpdateContentType() {
        return userAllowsAutoUpdateContentType;
    }
}
