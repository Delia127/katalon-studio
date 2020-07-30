package com.kms.katalon.platform.internal.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import com.kms.katalon.core.network.HttpClientProxyBuilder;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class RequestControllerImpl implements com.katalon.platform.api.controller.RequestController {

    @Override
    public HttpResponse send(HttpUriRequest request) throws KeyManagementException, MalformedURLException,
            URISyntaxException, IOException, GeneralSecurityException {
        HttpClient httpClient = HttpClientProxyBuilder.create(null).getClientBuilder().build();
        return httpClient.execute(request);
    }

    @Override
    public HttpResponse sendWithProxy(HttpUriRequest request) throws KeyManagementException, MalformedURLException,
            URISyntaxException, IOException, GeneralSecurityException {
        ProxyInformation systemProxy = ProxyPreferences.getSystemProxyInformation();
        HttpClient httpClient = HttpClientProxyBuilder.create(systemProxy).getClientBuilder().build();
        return httpClient.execute(request);
    }

}
