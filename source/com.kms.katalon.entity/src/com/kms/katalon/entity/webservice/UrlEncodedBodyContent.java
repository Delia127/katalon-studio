package com.kms.katalon.entity.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class UrlEncodedBodyContent implements HttpBodyContent {
    
    private String contentType = "application/x-www-form-urlencoded";
    
    private String charset = "UTF-8";
    
    private List<UrlEncodedBodyParameter> params = new ArrayList<>();

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
    
    public List<UrlEncodedBodyParameter> getParameters() {
        return params;
    }
    
    public void addParameter(UrlEncodedBodyParameter param) {
        params.add(param);
    }
    
    public void addParameter(int index, UrlEncodedBodyParameter param) {
        params.add(index, param);
    }
    
    public void removeParameter(UrlEncodedBodyParameter param) {
        params.remove(param);
    }
    
    public void removeEmptyParameters() {
        List<UrlEncodedBodyParameter> removedParams = params.stream()
                .filter(p -> StringUtils.isBlank(p.getName()) && StringUtils.isBlank(p.getValue()))
                .collect(Collectors.toList());
        params.removeAll(removedParams);
    }
}
