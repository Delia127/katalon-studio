package com.kms.katalon.core.webservice.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import com.google.common.net.UrlEscapers;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.exception.ConnectionTimeoutException;
import com.kms.katalon.core.webservice.exception.ResponseSizeLimitException;
import com.kms.katalon.core.webservice.exception.SocketTimeoutException;
import com.kms.katalon.core.webservice.helper.RestRequestMethodHelper;
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper;
import com.kms.katalon.core.webservice.support.UrlEncoder;
import com.kms.katalon.core.webservice.util.WebServiceCommonUtil;

public class RestfulClient extends BasicRequestor {

    private static final String TLS = "TLS";

    private static final String HTTPS = RequestHeaderConstants.HTTPS;
    
    private static final int MAX_REDIRECTS = 5;
    
    public RestfulClient(String projectDir, ProxyInformation proxyInfomation) {
        super(projectDir, proxyInfomation);
    }

    @Override
    public ResponseObject send(RequestObject request) throws Exception {
        ResponseObject responseObject;
        responseObject = sendRequest(request);
        return responseObject;
    }

    private ResponseObject sendRequest(RequestObject request) throws Exception {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        
        if (!request.isFollowRedirects()) {
            clientBuilder.disableRedirectHandling();
        } else {
            clientBuilder.setRedirectStrategy(new WebServiceRedirectStrategy());
        }
        
        configTimeout(clientBuilder, request);
        
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setConnectionManagerShared(true);
        
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith(HTTPS)) {
            SSLContext sc = SSLContext.getInstance(TLS);
            sc.init(getKeyManagers(), getTrustManagers(), null);
            //this will be overridden by setting connection manager for clientBuilder
            clientBuilder.setSSLContext(sc);
        }
        
        ProxyInformation proxyInfo = request.getProxy() != null ? request.getProxy() : proxyInformation;
        URL newUrl = new URL(request.getRestUrl());
        Proxy proxy = proxyInfo == null ? Proxy.NO_PROXY : ProxyUtil.getProxy(proxyInfo, newUrl);
        if (!Proxy.NO_PROXY.equals(proxy) || proxy.type() != Proxy.Type.DIRECT) {
            configureProxy(clientBuilder, proxyInfo);
        }

        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith(HTTPS)) {
            //this will be overridden by setting connection manager for clientBuilder
            clientBuilder.setSSLHostnameVerifier(getHostnameVerifier());
        }
        
        clientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        // copied from source
                Args.notNull(response, "HTTP response");
                final HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    final HeaderElement he = it.nextElement();
                    final String param = he.getName();
                    final String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (final NumberFormatException ignore) {}
                    }
                }
                // If the server indicates no timeout, then let it be 1ms so that connection is not kept alive
                // indefinitely
                return 1;
            }
        });
        
        BaseHttpRequest httpRequest = getHttpRequest(request);

        CloseableHttpClient httpClient = clientBuilder.build();
        
        // Default if not set
        setHttpConnectionHeaders(httpRequest, request);
        
        ResponseObject responseObject = response(httpClient, httpRequest, request);
        
        IOUtils.closeQuietly(httpClient);

        return responseObject;
    }

    private static boolean isBodySupported(String requestMethod) {
        return RestRequestMethodHelper.isBodySupported(requestMethod);
    }

    private static void setRequestMethod(CustomHttpMethodRequest httpRequest, String method) {
        httpRequest.setMethod(method);
    }
    
    private static BaseHttpRequest getHttpRequest(RequestObject request) throws UnsupportedOperationException, IOException {
        BaseHttpRequest httpRequest;
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

        return httpRequest;
    }

    private static String escapeUrl(String url) throws MalformedURLException {
        String escapedUrl = UrlEscapers.urlFragmentEscaper().escape(url);
        return escapedUrl;
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
    
    private ResponseObject response(CloseableHttpClient httpClient, BaseHttpRequest httpRequest, RequestObject requestObject) throws Exception {
        if (httpClient == null || httpRequest == null) {
            return null;
        }
        
        long startTime = System.currentTimeMillis();

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpRequest, getHttpContext());
        } catch (ConnectTimeoutException exception) {
            throw new ConnectionTimeoutException(exception);
        } catch (java.net.SocketTimeoutException exception) {
            throw new SocketTimeoutException(exception);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        long waitingTime = System.currentTimeMillis() - startTime;
        long contentDownloadTime = 0L;
        String responseBody = StringUtils.EMPTY;

        long headerLength = WebServiceCommonHelper.calculateHeaderLength(response);
        long bodyLength = 0L;
        
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            bodyLength = responseEntity.getContentLength();

            long totalResponseSize = headerLength + bodyLength;
            long maxResponseSize = requestObject.getMaxResponseSize();
            boolean isLimitResponseSize = WebServiceCommonUtil.isLimitedRequestResponseSize(maxResponseSize);
            if (isLimitResponseSize && totalResponseSize > maxResponseSize) {
                httpRequest.abort();
                throw new ResponseSizeLimitException();
            }

            startTime = System.currentTimeMillis();
            try {
                responseBody = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            } catch (Exception e) {
                responseBody = ExceptionUtils.getFullStackTrace(e);
            }
            contentDownloadTime = System.currentTimeMillis() - startTime;
        }

        ResponseObject responseObject = new ResponseObject(responseBody);
        responseObject.setContentType(getResponseContentType(response));
        responseObject.setHeaderFields(getResponseHeaderFields(response));
        responseObject.setStatusCode(statusCode);
        responseObject.setResponseBodySize(bodyLength);
        responseObject.setResponseHeaderSize(headerLength);
        responseObject.setWaitingTime(waitingTime);
        responseObject.setContentDownloadTime(contentDownloadTime);
        
        setBodyContent(response, responseBody, responseObject);

        IOUtils.closeQuietly(response);
        
        return responseObject;
    }
}
