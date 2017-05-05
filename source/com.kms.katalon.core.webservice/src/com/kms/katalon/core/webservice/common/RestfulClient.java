package com.kms.katalon.core.webservice.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.webservice.support.UrlEncoder;

public class RestfulClient implements Requestor {

    private static final String DEFAULT_USER_AGENT = "Katalon Studio";

    private static final String HTTP_USER_AGENT = "User-Agent";

    @Override
    public ResponseObject send(RequestObject request) throws Exception {
        ResponseObject responseObject;
        if ("GET".equalsIgnoreCase(request.getRestRequestMethod())) {
            responseObject = sendGetRequest(request);
        } else if ("DELETE".equalsIgnoreCase(request.getRestRequestMethod())) {
            responseObject = sendDeleteRequest(request);
        } else {
            // POST, PUT are technically the same
            responseObject = sendPostRequest(request);
        }
        return responseObject;
    }

    private ResponseObject sendGetRequest(RequestObject request) throws Exception {
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith("https")) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, getTrustManagers(), new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }

        // If there are some parameters, they should be append after the Service URL
        processRequestParams(request);

        URL url = new URL(request.getRestUrl());
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(getProxy());
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith("https")) {
            ((HttpsURLConnection) httpConnection).setHostnameVerifier(getHostnameVerifier());
        }
        httpConnection.setRequestMethod(request.getRestRequestMethod());

        // Default if not set
        httpConnection.setRequestProperty(HTTP_USER_AGENT, DEFAULT_USER_AGENT);
        for (TestObjectProperty property : request.getHttpHeaderProperties()) {
            httpConnection.setRequestProperty(property.getName(), property.getValue());
        }

        return response(httpConnection);
    }

    private ResponseObject sendPostRequest(RequestObject request) throws Exception {
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith("https")) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, getTrustManagers(), new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }

        // If there are some parameters, they should be append after the Service URL
        processRequestParams(request);

        URL url = new URL(request.getRestUrl());
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(getProxy());
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith("https")) {
            ((HttpsURLConnection) httpConnection).setHostnameVerifier(getHostnameVerifier());
        }
        httpConnection.setRequestMethod(request.getRestRequestMethod());

        // Default if not set
        httpConnection.setRequestProperty(HTTP_USER_AGENT, DEFAULT_USER_AGENT);
        for (TestObjectProperty property : request.getHttpHeaderProperties()) {
            httpConnection.setRequestProperty(property.getName(), property.getValue());
        }
        httpConnection.setDoOutput(true);

        // Send post request
        OutputStream os = httpConnection.getOutputStream();
        os.write((request.getHttpBody() == null ? "" : request.getHttpBody()).getBytes());
        os.flush();
        os.close();

        return response(httpConnection);
    }

    private ResponseObject sendDeleteRequest(RequestObject request) throws Exception {
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith("https")) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, getTrustManagers(), new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }

        // If there are some parameters, they should be append after the Service URL
        processRequestParams(request);

        URL url = new URL(request.getRestUrl());
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(getProxy());
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith("https")) {
            ((HttpsURLConnection) httpConnection).setHostnameVerifier(getHostnameVerifier());
        }

        httpConnection.setRequestMethod(request.getRestRequestMethod());
        // Default if not set
        httpConnection.setRequestProperty(HTTP_USER_AGENT, DEFAULT_USER_AGENT);
        for (TestObjectProperty property : request.getHttpHeaderProperties()) {
            httpConnection.setRequestProperty(property.getName(), property.getValue());
        }

        return response(httpConnection);
    }

    private static void processRequestParams(RequestObject request) throws MalformedURLException {
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
            request.setRestUrl(request.getRestUrl() + (StringUtils.isEmpty(url.getQuery()) ? "?" : "&")
                    + paramString.toString());
        }
    }

    private ResponseObject response(HttpURLConnection conn) throws Exception {
        if (conn == null) {
            return null;
        }

        int statusCode = conn.getResponseCode();
        StringBuffer sb = new StringBuffer();
        try (InputStream inputStream = (statusCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
        }

        ResponseObject responseObject = new ResponseObject(sb.toString());
        responseObject.setContentType(conn.getContentType());
        responseObject.setHeaderFields(conn.getHeaderFields());
        responseObject.setStatusCode(statusCode);

        conn.disconnect();

        return responseObject;
    }
}
