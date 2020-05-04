package com.kms.katalon.core.webservice.common;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ByteArrayEntity;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.webservice.helper.RestRequestMethodHelper;
import com.kms.katalon.core.webservice.support.UrlEncoder;

public class RestfulClient extends BasicRequestor {
    
    public RestfulClient(String projectDir, ProxyInformation proxyInfomation) {
        super(projectDir, proxyInfomation);
    }

    private boolean isBodySupported(String requestMethod) {
        return RestRequestMethodHelper.isBodySupported(requestMethod);
    }

    private void setRequestMethod(CustomHttpMethodRequest httpRequest, String method) {
        httpRequest.setMethod(method);
    }
    
    @Override
    protected HttpUriRequest buildHttpRequest(RequestObject request) throws Exception {
        HttpUriRequest httpRequest;
        String url = request.getRestUrl();
        if (isBodySupported(request.getRestRequestMethod()) && request.getBodyContent() != null) {
            httpRequest = new DefaultHttpEntityEnclosingRequest(url);
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            request.getBodyContent().writeTo(outstream);
            byte[] bytes = outstream.toByteArray();
            ByteArrayEntity entity = new ByteArrayEntity(bytes);
            entity.setChunked(false);
            ((DefaultHttpEntityEnclosingRequest) httpRequest)
                    .setEntity(entity);
        } else {
            httpRequest = new DefaultHttpRequest(url);
        }

        setRequestMethod(httpRequest, request.getRestRequestMethod());
        
        setHttpConnectionHeaders(httpRequest, request);
        
        return httpRequest;
    }

    public static void processRequestParams(RequestObject request) throws MalformedURLException {
        StringBuilder paramString = new StringBuilder();
        for (TestObjectProperty property : request.getRestParameters()) {
            if (StringUtils.isEmpty(property.getName())) {
                continue;
            }
            if (!StringUtils.isEmpty(paramString.toString())) {
                paramString.append("&");
            }
            paramString.append(UrlEncoder.encode(property.getName()));
            paramString.append("=");
            paramString.append(UrlEncoder.encode(property.getValue()));
        }
        if (!StringUtils.isEmpty(paramString.toString())) {
            URL url = new URL(request.getRestUrl());
            request.setRestUrl(
                    request.getRestUrl() + (StringUtils.isEmpty(url.getQuery()) ? "?" : "&") + paramString.toString());
        }
    }
}
