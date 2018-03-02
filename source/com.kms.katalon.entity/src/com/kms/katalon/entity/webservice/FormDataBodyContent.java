package com.kms.katalon.entity.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class FormDataBodyContent implements HttpBodyContent {

    private String contentType = "multipart/form-data";
    
    private String charset = "UTF-8";
    
    private List<FormDataBodyParameter> params = new ArrayList<>();

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
        return charset;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public List<FormDataBodyParameter> getParameters() {
        return params;
    }
    
    public void addParameter(FormDataBodyParameter param) {
        params.add(param);
    }
    
    public void addParameter(int index, FormDataBodyParameter param) {
        params.add(index, param);
    }
    
    public void removeParameter(FormDataBodyParameter param) {
        params.remove(param);
    }
    
    public void removeEmptyParameters() {
        List<FormDataBodyParameter> removedParams = params.stream()
                .filter(p -> StringUtils.isBlank(p.getName()) && StringUtils.isBlank(p.getValue()))
                .collect(Collectors.toList());
        params.removeAll(removedParams);
    }
}
