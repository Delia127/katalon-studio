package com.kms.katalon.integration.qtest.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.exception.QTestIOException;

public class QTestHttpRequestHelper {

    public static String getToken(IQTestCredential credential, String url) throws QTestIOException {
        CloseableHttpClient client = null;
        try {
            client = HttpClientBuilder.create().build();
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("grant_type", "password", ContentType.TEXT_PLAIN);
            builder.addTextBody("username", credential.getUsername(), ContentType.TEXT_PLAIN);
            builder.addTextBody("password", credential.getPassword(), ContentType.TEXT_PLAIN);
            builder.setBoundary(Long.toHexString(System.currentTimeMillis()));

            HttpEntity entity = builder.build();
            HttpPost post = new HttpPost(credential.getServerUrl() + url);

            String authEncoded = new Base64().encodeAsString((credential.getUsername() + ":").getBytes());
            post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + authEncoded);
            post.setHeader("Content-Disposition", "form-data;");

            CloseableHttpResponse response = null;
            try {
                post.setEntity(entity);

                response = client.execute(post);

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } catch (IOException ex) {
                throw new QTestIOException(ex);
            } finally {
                IOUtils.closeQuietly(response);
            }
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public static String sendPostRequest(IQTestCredential credential, String url, List<NameValuePair> postParams)
            throws QTestIOException {
        CloseableHttpClient client = null;
        try {
            client = HttpClientBuilder.create().build();

            Map<String, String> cookies = new HashMap<String, String>();
            doLogin(credential, client, cookies);
            String result = doPost(client, credential.getServerUrl(), url, postParams, cookies);

            return result;
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public static String sendGetRequest(IQTestCredential credential, String url) throws QTestIOException {
        CloseableHttpClient client = null;
        try {
            client = HttpClientBuilder.create().build();
            Map<String, String> cookies = new HashMap<String, String>();
            doLogin(credential, client, cookies);

            String result = doGet(client, credential.getServerUrl(), url, cookies);

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
            if (index > 0)
                builder.append(",");
            builder.append("\"").append(entry.getKey()).append("\"").append(":");

            if (useBrackets)
                builder.append("[");
            String value = String.valueOf(entry.getValue());
            if (entry.getValue() instanceof String) {
                value = "\"" + value + "\"";
            } else if (entry.getValue() instanceof Map<?, ?>) {
                value = createDataBody((Map<String, Object>) entry.getValue(), useBrackets);
            }
            builder.append(value);

            if (useBrackets)
                builder.append("]");

            index++;
        }
        builder.append("}]");
        return builder.toString();
    }

    public static void doLogin(IQTestCredential credential, CloseableHttpClient client, Map<String, String> cookies)
            throws QTestIOException {
        doGet(client, credential.getServerUrl(), "/portal/loginform", cookies);
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("j_username", credential.getUsername()));
        postParams.add(new BasicNameValuePair("j_password", credential.getPassword()));

        doPost(client, credential.getServerUrl(), "/login?redirect=%2Fportal%2Fproject", postParams, cookies);
        doGet(client, credential.getServerUrl(), "/portal/project", cookies);
    }

    private static String doPost(CloseableHttpClient client, String serverUrl, String url,
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

            return result.toString();
        } catch (IOException ex) {
            throw new QTestIOException(ex);
        } finally {
            IOUtils.closeQuietly(response);
        }
    }

    private static String doGet(CloseableHttpClient client, String serverUrl, String url, Map<String, String> cookies)
            throws QTestIOException {
        HttpGet request = new HttpGet(serverUrl + url);

        request.setHeader("User-Agent", "Mozilla/5.0");
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Cookie", cookiesString(cookies));
        request.setHeader("X-CSRF-Token", "0.0");

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

            return result.toString();
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
            String composedValue = header.getValue();
            int seperatingIndex = composedValue.indexOf("=");
            String name = composedValue.substring(0, seperatingIndex);
            String value = composedValue.substring(seperatingIndex + 1);
            cookies.put(name, value);
        }
    }
}
