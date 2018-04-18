package com.kms.katalon.entity.webservice;

import org.apache.commons.lang3.StringUtils;

public class TextBodyContent implements HttpBodyContent {
    
    private static final String DF_CONTENT_TYPE = "text/plain";

    private String text;
    
    private String contentType = DF_CONTENT_TYPE;
    
    private String charset = "UTF-8";

    @Override
    public String getContentType() {
        if (StringUtils.isEmpty(contentType)) {
            return DF_CONTENT_TYPE;
        }
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
