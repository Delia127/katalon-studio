package com.kms.katalon.integration.qtest.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.core.network.HttpClientProxyBuilder;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.exception.QTestAPIConnectionException;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestIOException;

public class QTestHttpRequestHelper {

    private QTestHttpRequestHelper() {
        //Disable default constructor
    }
    
    public static String getV7Token(IQTestCredential credential, String url) throws QTestException {
        HttpResponseResult reponseResult = internallyGetV7Token(credential, url);
        int statusCode = reponseResult.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            return reponseResult.getResult();
        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            // return internallyGetV7Token(credential, url).getResult();
            // Try to parse and get qTest error message
            try {
                JsonObject jo = new JsonObject(reponseResult.getResult());
                throw new QTestAPIConnectionException(jo.getString("error_description"));
            } catch (JsonException e) {}
            throw new QTestAPIConnectionException(reponseResult.getResult());
        } else {
            throw new QTestAPIConnectionException(reponseResult.getResult());
        }
    }

    private static HttpResponseResult internallyGetV7Token(IQTestCredential credential, String url)
            throws QTestIOException {
        CloseableHttpClient client = null;
        try {
            client = HttpClientProxyBuilder.create(ProxyPreferences.getProxyInformation()).getAcceptedSelfSignedCertClientBuilder().build();
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("grant_type", "password"));
            postParams.add(new BasicNameValuePair("username", credential.getUsername()));
            postParams.add(new BasicNameValuePair("password", credential.getPassword()));

            HttpPost post = new HttpPost(credential.getServerUrl() + url);

            String authEncoded = Base64.getEncoder().encodeToString(("katalon-user :").getBytes());
            post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + authEncoded);
            post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());

            CloseableHttpResponse response = null;
            HttpResponseResult reponseResult = new HttpResponseResult();
            try {
                post.setEntity(new UrlEncodedFormEntity(postParams));

                response = client.execute(post);

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                reponseResult.setHeaders(response.getAllHeaders());
                reponseResult.setResult(result.toString());
                reponseResult.setStatusLine(response.getStatusLine());

                return reponseResult;
            } catch (IOException ex) {
                throw new QTestIOException(ex);
            } finally {
                IOUtils.closeQuietly(response);
            }
        } catch (URISyntaxException | GeneralSecurityException | IOException e) {
            throw new QTestIOException(ExceptionsUtil.getStackTraceForThrowable(e));
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public static String sendPostRequest(IQTestCredential credential, String url, List<NameValuePair> postParams)
            throws QTestException {
        CloseableHttpClient client = null;
        try {
            client = HttpClientProxyBuilder.create(ProxyPreferences.getProxyInformation()).getAcceptedSelfSignedCertClientBuilder().build();

            Map<String, String> cookies = new HashMap<String, String>();
            doLogin(credential, client, cookies);
            String result = doPost(client, credential.getServerUrl(), url, postParams, cookies).getResult();
            doLogout(credential, client, cookies);

            return result;
        } catch (URISyntaxException | GeneralSecurityException | IOException e) {
            throw new QTestIOException(ExceptionsUtil.getStackTraceForThrowable(e));
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public static String sendGetRequest(IQTestCredential credential, String url) throws QTestException {
        CloseableHttpClient client = null;
        try {
            client = HttpClientBuilder.create().build();
            Map<String, String> cookies = new HashMap<String, String>();
            doLogin(credential, client, cookies);

            String result = doGet(client, credential.getServerUrl(), url, cookies).getResult();
            doLogout(credential, client, cookies);
            return result;
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    /**
     * Body of a request sent to qTest via HTTP connection must be JSON format. This function will transform a map of
     * properties to JSON format.
     * 
     * @param mapProperties
     * @return: JSON String.
     */
    @SuppressWarnings("unchecked")
    public static String createDataBody(Map<String, Object> mapProperties, boolean useBrackets) {
        StringBuilder builder = new StringBuilder("[{");
        int index = 0;
        for (Entry<String, Object> entry : mapProperties.entrySet()) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append("\"").append(entry.getKey()).append("\"").append(":");
            
            String value = String.valueOf(entry.getValue());
            if (entry.getValue() instanceof String) {
                value = "\"" + value + "\"";
            } else if (entry.getValue() instanceof Map<?, ?>) {
                value = createDataBody((Map<String, Object>) entry.getValue(), useBrackets);
            }
            builder.append(value);

            if (useBrackets) {
                builder.insert(0, "[").append("]");
            }

            index++;
        }
        builder.append("}]");
        return builder.toString();
    }

    

    public static void doLogin(IQTestCredential credential, CloseableHttpClient client, Map<String, String> cookies)
            throws QTestException {
        doGet(client, credential.getServerUrl(), "/portal/loginform", cookies);
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("j_username", credential.getUsername()));
        postParams.add(new BasicNameValuePair("j_password", credential.getPassword()));

        doPost(client, credential.getServerUrl(),
                "/login?redirect=%2Fportal%2Fproject", postParams, cookies);

        // Access portal project
        doGet(client, credential.getServerUrl(), "/portal/project", cookies);
    }

    public static void doLogout(IQTestCredential credential, CloseableHttpClient client, Map<String, String> cookies)
            throws QTestIOException {
        doPost(client, credential.getServerUrl(), "/logout", new ArrayList<NameValuePair>(), cookies);
    }

    private static HttpResponseResult doPost(CloseableHttpClient client, String serverUrl, String url,
            List<NameValuePair> postParams, Map<String, String> cookies) throws QTestIOException {

        HttpPost post = new HttpPost(serverUrl + url);

        // add header
        post.setHeader("Host", serverUrl.replace("http://", "").replace("https://", ""));
        post.setHeader("User-Agent", "Mozilla/5.0");
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Cookie", cookiesString(cookies));
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.setHeader("X-CSRF-Token", "0.0");

        HttpResponseResult reponseResult = new HttpResponseResult();
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(postParams));

            response = client.execute(post);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            // set cookies
            addCookies(response, cookies);

            reponseResult.setHeaders(response.getAllHeaders());
            reponseResult.setResult(result.toString());
            reponseResult.setStatusLine(response.getStatusLine());

            return reponseResult;
        } catch (IOException ex) {
            throw new QTestIOException(ex);
        } finally {
            IOUtils.closeQuietly(response);
        }
    }

    private static HttpResponseResult doGet(CloseableHttpClient client, String serverUrl, String url,
            Map<String, String> cookies) throws QTestIOException {
        HttpGet request = new HttpGet(serverUrl + url);

        request.setHeader("User-Agent", "Mozilla/5.0");
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Cookie", cookiesString(cookies));
        request.setHeader("Connection", "keep-alive");
        request.setHeader("X-CSRF-Token", "0.0");

        HttpResponseResult reponseResult = new HttpResponseResult();
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            // set cookies
            addCookies(response, cookies);
            reponseResult.setHeaders(response.getAllHeaders());
            reponseResult.setResult(result.toString());
            reponseResult.setStatusLine(response.getStatusLine());

            return reponseResult;
        } catch (IOException ex) {
            throw new QTestIOException(ex);
        } finally {
            IOUtils.closeQuietly(response);
        }
    }

    private static String cookiesString(Map<String, String> cookies) {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> cookie : cookies.entrySet()) {
            builder.append(cookie.getKey()).append("=").append(cookie.getValue()).append(";");
        }
        return builder.toString();
    }

    private static void addCookies(HttpResponse response, Map<String, String> cookies) {
        for (Header header : response.getHeaders("Set-Cookie")) {
            for (String composedValue : header.getValue().trim().split(";")) {
                int seperatingIndex = composedValue.indexOf("=");
                if (seperatingIndex == -1) {
                    cookies.put(composedValue, "");
                } else {
                    String name = composedValue.substring(0, seperatingIndex);
                    String value = composedValue.substring(seperatingIndex + 1);
                    cookies.put(name, value);
                }
            }
        }
    }
}
