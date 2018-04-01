package com.kms.katalon.core.testobject.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.kms.katalon.core.testobject.FormDataBodyParameter;
import com.kms.katalon.core.testobject.HttpBodyContent;

public class HttpFormDataBodyContent implements HttpBodyContent {

    private static final String CONTENT_TYPE = "multipart/form-data;boundary=kat";
    
    private static final String CHARSET = "UTF-8";
    
    private static final String BOUNDARY = "kat";
    
    private MultipartEntityBuilder multipartEntityBuilder;
    
    private HttpEntity multipartEntity;
    
    public HttpFormDataBodyContent(List<FormDataBodyParameter> parameters) throws FileNotFoundException {
        multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
        multipartEntityBuilder.setCharset(Charset.forName(CHARSET));
        for (FormDataBodyParameter parameter : parameters) {
            if (parameter.getType().equals(FormDataBodyParameter.PARAM_TYPE_FILE)) {
                multipartEntityBuilder.addBinaryBody(
                        parameter.getName(), 
                        new FileInputStream(parameter.getValue()), 
                        ContentType.DEFAULT_BINARY,
                        parameter.getValue());
            } else {
                multipartEntityBuilder.addTextBody(parameter.getName(), parameter.getValue());
            }
        }
        multipartEntityBuilder.setBoundary(BOUNDARY);
        multipartEntity = multipartEntityBuilder.build();
    }
    
    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public long getContentLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getContentEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException, UnsupportedOperationException {
        return multipartEntity.getContent();
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        multipartEntity.writeTo(outstream);
    }

}
