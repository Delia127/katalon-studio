package com.kms.katalon.entity.webservice;

public class FileBodyContent implements HttpBodyContent {

    private String filePath;

    private long fileSize;

    private String contentType = "";

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public long getContentLength() {
        return fileSize;
    }

    @Override
    public String getCharset() {
        // Nothing to do
        return null;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
