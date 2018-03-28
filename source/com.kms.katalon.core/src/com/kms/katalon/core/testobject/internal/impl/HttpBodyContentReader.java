package com.kms.katalon.core.testobject.internal.impl;

import java.io.FileNotFoundException;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.exception.KatalonRuntimeException;
import com.kms.katalon.core.testobject.HttpBodyContent;
import com.kms.katalon.core.testobject.impl.HttpBodyType;
import com.kms.katalon.core.testobject.impl.HttpFileBodyContent;
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.util.internal.PathUtil;

public class HttpBodyContentReader {
    private HttpBodyContentReader() {
        // Disable default constructor
    }

    public static HttpBodyContent fromSource(String httpBodyType, String httpBodyContent, String projectDir)
            throws KatalonRuntimeException {
        switch (HttpBodyType.fromType(httpBodyType)) {
            case TEXT:
                InternalTextBodyContent textBodyContent = JsonUtil.fromJson(httpBodyContent,
                        InternalTextBodyContent.class);
                return new HttpTextBodyContent(textBodyContent.getText(), textBodyContent.getCharset(),
                        textBodyContent.getContentType());
            case FILE:
                InternalFileBodyContent fileBodyContent = JsonUtil.fromJson(httpBodyContent,
                        InternalFileBodyContent.class);
                try {
                    String filePath = fileBodyContent.getFilePath();
                    String absoluteFilePath = filePath;
                    if (StringUtils.isNotEmpty(filePath)) {
                        absoluteFilePath = PathUtil.relativeToAbsolutePath(filePath, projectDir);
                    }
                    return new HttpFileBodyContent(absoluteFilePath);
                } catch (IllegalArgumentException | FileNotFoundException e) {
                    throw new KatalonRuntimeException(e);
                }
            case FORM_DATA:
                // TODO: KAT-3024
                break;
            case URL_ENCODED:
                // TODO: KAT-3025
                break;
            default:
                break;
        }
        throw new KatalonRuntimeException(MessageFormat.format("There is no implementation for {0}", httpBodyType));
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

    private class InternalFileBodyContent implements InternalHttpBodyContent {

        private String filePath;

        private long fileSize;

        private String contentType = "";

        @Override
        public String getContentType() {
            return contentType;
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
    }

}
