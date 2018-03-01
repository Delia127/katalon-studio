package com.kms.katalon.entity.webservice;

public class FileBodyContent implements HttpBodyContent {

    private String absoluteFilePath;

    private String contentType = "file";

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getContentLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCharset() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }
}
