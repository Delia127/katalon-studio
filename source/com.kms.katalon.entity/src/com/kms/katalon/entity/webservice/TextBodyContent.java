package com.kms.katalon.entity.webservice;

public class TextBodyContent implements HttpBodyContent {
    
    private String text;
    
    private String contentType = "text/plain";
    
    private String charset = "UTF-8";

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getContentLength() {
        if (text == null) {
            return -1L;
        }
        return text.length();
    }

    @Override
    public String getCharset() {
        return charset;
    }

    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}
