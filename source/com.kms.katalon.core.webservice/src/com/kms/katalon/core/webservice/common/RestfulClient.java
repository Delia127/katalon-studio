package com.kms.katalon.core.webservice.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang.StringUtils;
import org.testng.util.Strings;

import com.google.common.net.MediaType;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.helper.RestRequestMethodHelper;
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper;
import com.kms.katalon.core.webservice.support.UrlEncoder;

public class RestfulClient extends BasicRequestor {

    private static final String SSL = RequestHeaderConstants.SSL;

    private static final String HTTPS = RequestHeaderConstants.HTTPS;

    private static final String DEFAULT_USER_AGENT = GlobalStringConstants.APP_NAME;

    private static final String HTTP_USER_AGENT = RequestHeaderConstants.USER_AGENT;
    
    private static final int MAX_REDIRECTS = 5;
    
    private static final String[] BODY_UNSUPPORTED_METHODS = new String[] {
        RequestHeaderConstants.GET, RequestHeaderConstants.HEAD
    };
    
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
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith(HTTPS)) {
            SSLContext sc = SSLContext.getInstance(SSL);
            sc.init(getKeyManagers(), getTrustManagers(), null);
            SSLSocketFactory socketFactory = sc.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
        }

        // If there are some parameters, they should be append after the Service URL
        processRequestParams(request);

        URL url = new URL(request.getRestUrl());
        
        Proxy proxy = request.getProxy() != null ? request.getProxy() : getProxy();
        
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(proxy);
        if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith(HTTPS)) {
            ((HttpsURLConnection) httpConnection).setHostnameVerifier(getHostnameVerifier());
        }
        
        String requestMethod = request.getRestRequestMethod();
        setRequestMethod(httpConnection, request.getRestRequestMethod());

        // Default if not set
        httpConnection.setRequestProperty(HTTP_USER_AGENT, DEFAULT_USER_AGENT);
        setHttpConnectionHeaders(httpConnection, request);
        
        if (isBodySupported(requestMethod) && request.getBodyContent() != null) {
            httpConnection.setDoOutput(true);
            
            // Send post request
            OutputStream os = httpConnection.getOutputStream();
            request.getBodyContent().writeTo(os);
            os.flush();
            os.close();
        }

        ResponseObject responseObject = response(httpConnection);
        boolean redirect = false;
        int statusCode = responseObject.getStatusCode();
        if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
            || statusCode == HttpURLConnection.HTTP_MOVED_PERM
            || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
            redirect = true;
        }
        
        if (redirect) {
            String newUrl = httpConnection.getHeaderField("location");
            if (!StringUtils.isBlank(newUrl)) {
                request.setRestUrl(newUrl);
                request.setRedirectTimes(request.getRedirectTimes() + 1);
                if (request.isFollowRedirects() && request.getRedirectTimes() <= MAX_REDIRECTS) {
                    responseObject = sendRequest(request);
                }
            }
        }
        
        return responseObject;
    }
    
    private boolean isBodySupported(String requestMethod) {
        return RestRequestMethodHelper.isBodySupported(requestMethod);
    }

    /**
     * HttpURLConnection will throw ProtocolException when setting a request method which is not 
     * GET, POST, HEAD, OPTIONS, PUT, DELETE, or TRACE. Use this workaround for unsupported methods.
     */
    private static void setRequestMethod(HttpURLConnection connection, String method) 
            throws ProtocolException {
        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException ex) {
                try {
                    Field methodField = HttpURLConnection.class.getDeclaredField("method");
                    methodField.setAccessible(true);
                    if (connection instanceof HttpsURLConnection) {
                        try {
                            Field delegateField = connection.getClass().getDeclaredField("delegate");
                            delegateField.setAccessible(true);
                            
                            Object delegateConnection = delegateField.get(connection);
                            if (delegateConnection instanceof HttpURLConnection) {
                                methodField.set(delegateConnection, method);
                            }

                            Field httpsURLConnectionField = delegateConnection.getClass()
                                    .getDeclaredField("httpsURLConnection");
                            httpsURLConnectionField.setAccessible(true);
                            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpsURLConnectionField
                                    .get(delegateConnection);

                            methodField.set(httpsURLConnection, method);
                        } catch (Exception ignored) {
                            
                        }
                        
                    }
                    
                    methodField.set(connection, method);
                } catch (Exception e) {
                    throw ex;
                }
        }
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

    private ResponseObject response(HttpURLConnection conn) throws Exception {
        if (conn == null) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        int statusCode = conn.getResponseCode();
        long waitingTime = System.currentTimeMillis() - startTime;
        long contentDownloadTime = 0L;
        StringBuffer sb = new StringBuffer();

        char[] buffer = new char[1024];
        long bodyLength = 0L;
        
        Charset charset = StandardCharsets.UTF_8;
        try {
	        String contentType = conn.getContentType();
	        if (Strings.isNotNullAndNotEmpty(contentType)) {
	        	MediaType mediaType = MediaType.parse(contentType);
	        	charset = mediaType.charset().or(StandardCharsets.UTF_8);
	        }
        } catch (Exception e) {
        	// ignored - don't let tests fail just because charset could not be detected
        }
        
        try (InputStream inputStream = (statusCode >= 400) ? conn.getErrorStream() : conn.getInputStream()) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
                int len = 0;
                startTime = System.currentTimeMillis();
                while ((len = reader.read(buffer)) != -1) {
                    contentDownloadTime += System.currentTimeMillis() - startTime;
                    sb.append(buffer, 0, len);
                    bodyLength += len;
                    startTime = System.currentTimeMillis();
                }
            }
        }

        long headerLength = WebServiceCommonHelper.calculateHeaderLength(conn);

        ResponseObject responseObject = new ResponseObject(sb.toString());
        responseObject.setContentType(conn.getContentType());
        responseObject.setHeaderFields(conn.getHeaderFields());
        responseObject.setStatusCode(statusCode);
        responseObject.setResponseBodySize(bodyLength);
        responseObject.setResponseHeaderSize(headerLength);
        responseObject.setWaitingTime(waitingTime);
        responseObject.setContentDownloadTime(contentDownloadTime);
        
        setBodyContent(conn, sb, responseObject);
        conn.disconnect();

        return responseObject;
    }

}
