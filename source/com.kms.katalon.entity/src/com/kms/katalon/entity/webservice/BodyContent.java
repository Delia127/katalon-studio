package com.kms.katalon.entity.webservice;

import java.util.ArrayList;
import java.util.List;

public class BodyContent<P> implements HttpBodyContent{

    private String contentType;
    
    private String charset;
    
    private List<P> parameters = new ArrayList<>();
    
    @Override
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public List<P> getParameters() {
        return parameters;
    }
    
    public void addParameter(P param) {
        parameters.add(param);
    }
    
    public void addParameter(int index, P param) {
        parameters.add(index, param);
    }
    
    public void removeParameter(P param) {
        parameters.remove(param);
    }
}
