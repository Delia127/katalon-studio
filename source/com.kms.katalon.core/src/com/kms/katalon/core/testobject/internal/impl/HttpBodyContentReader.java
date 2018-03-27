package com.kms.katalon.core.testobject.internal.impl;

import com.kms.katalon.core.testobject.HttpBodyContent;
import com.kms.katalon.core.testobject.impl.HttpBodyType;
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent;
import com.kms.katalon.core.util.internal.JsonUtil;

public class HttpBodyContentReader {
    private HttpBodyContentReader() {
        // Disable default constructor
    }

    public static HttpBodyContent fromSource(String httpBodyType, String httpBodyContent) {
        switch (HttpBodyType.fromType(httpBodyType)) {
            case TEXT:
                InternalTextBodyContent textBodyContent = JsonUtil.fromJson(httpBodyContent,
                        InternalTextBodyContent.class);
                return new HttpTextBodyContent(textBodyContent.getText(), textBodyContent.getCharset(),
                        textBodyContent.getContentType());
            case FILE:
                // TODO: KAT-3026
                break;
            case FORM_DATA:
                // TODO: KAT-3024
                break;
            case URL_ENCODED:
                // TODO: KAT-3025
                break;
            default:
                break;
        }
        return null;
    }

    private interface InternalHttpBodyContent {

        String getContentType();

        long getContentLength();

        String getCharset();
    }

    private class InternalTextBodyContent implements InternalHttpBodyContent {

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

    }

}
