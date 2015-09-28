package com.kms.katalon.integration.qtest.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.kms.katalon.integration.qtest.exception.QTestIOException;

@SuppressWarnings("restriction")
public class QTestHttpRequestHelper {

    public static String sendPostRequest(String serverUrl, String url, String username, String password,
            List<NameValuePair> postParams) throws QTestIOException {
        HttpClient client = HttpClientBuilder.create().build();

        Map<String, String> cookies = new HashMap<String, String>();
        doLogin(client, serverUrl, cookies, username, password);
        String result = doPost(client, serverUrl, url, postParams, cookies);

        return result;
    }

    public static String sendGetRequest(String serverUrl, String url, String username, String password)
            throws QTestIOException {
        HttpClient client = HttpClientBuilder.create().build();

        Map<String, String> cookies = new HashMap<String, String>();
        doLogin(client, serverUrl, cookies, username, password);

        String result = doGet(client, serverUrl, url, cookies);

        return result;
    }

    /**
     * Body of a request sent to qTest via HTTP connection must be JSON format.
     * This function will transform a map of properties to JSON format.
     * 
     * @param mapProperties
     * @return: JSON String.
     */
    @SuppressWarnings("unchecked")
    public static String createDataBody(Map<String, Object> mapProperties, boolean useBrackets) {
        StringBuilder builder = new StringBuilder("[{");
        int index = 0;
        for (Entry<String, Object> entry : mapProperties.entrySet()) {
            if (index > 0) builder.append(",");
            builder.append("\"").append(entry.getKey()).append("\"").append(":");

            if (useBrackets) builder.append("[");
            String value = String.valueOf(entry.getValue());
            if (entry.getValue() instanceof String) {
                value = "\"" + value + "\"";
            } else if (entry.getValue() instanceof Map<?, ?>) {
                value = createDataBody((Map<String, Object>) entry.getValue(), useBrackets);
            }
            builder.append(value);

            if (useBrackets) builder.append("]");

            index++;
        }
        builder.append("}]");
        return builder.toString();
    }

    public static void doLogin(HttpClient client, String serverUrl, Map<String, String> cookies, String username,
            String password) throws QTestIOException {
        doGet(client, serverUrl, "/portal/loginform", cookies);
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("j_username", username));
        postParams.add(new BasicNameValuePair("j_password", password));

        doPost(client, serverUrl, "/login?redirect=%2Fportal%2Fproject", postParams, cookies);
        doGet(client, serverUrl, "/portal/project", cookies);
    }

    private static String doPost(HttpClient client, String serverUrl, String url, List<NameValuePair> postParams,
            Map<String, String> cookies) throws QTestIOException {

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

        HttpResponse response = null;
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
            if (response != null) {
                closeQuietly(response);
            }
        }
    }

    public static String doGet(HttpClient client, String serverUrl, String url, Map<String, String> cookies)
            throws QTestIOException {
        HttpGet request = new HttpGet(serverUrl + url);

        request.setHeader("User-Agent", "Mozilla/5.0");
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Cookie", cookiesString(cookies));
        request.setHeader("X-CSRF-Token", "0.0");

        HttpResponse response = null;
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
            if (response != null) {
                closeQuietly(response);
            }
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

    private static void closeQuietly(HttpResponse response) {
        try {
            response.getEntity().getContent().close();
        } catch (IOException e) {
            // Ignore it
        }
    }
}
