package com.kms.katalon.console.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kms.katalon.console.constants.ConsoleMessageConstants;
import com.kms.katalon.logging.LogUtil;

public class ServerAPICommunicationUtil {
    public static final String URL_API = "https://backend-dev.katalon.com/api";

    private static final String POST = "POST";

    private static final String GET = "GET";

    public static String post(String function, String jsonData) throws IOException {
        return invoke(POST, function, jsonData);
    }

    public static String invoke(String method, String function, String jsonData) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = createConnection(method, URL_API + function);
            return sendAndReceiveData(connection, jsonData);
        } catch (Exception ex) {
            LogUtil.logError(ex);
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getInformation(String url, JsonObject jsonObject) {
        try {
            return invoke(GET, url, jsonObject.toString());
        } catch (IOException ex) {
            return null;
        }
    }

    public static String getInformation(String url) {
        try {
            return invoke(GET, url, null);
        } catch (IOException ex) {
            return null;
        }
    }

    public static JsonObject getJsonInformation(String url, JsonObject jsonObject) {
        try {
            return new JsonParser().parse(invoke(GET, url, jsonObject.toString())).getAsJsonObject();
        } catch (IOException ex) {
            return null;
        }
    }

    public static JsonObject getJsonInformation(String url, String jsonData) {
        try {
            return new JsonParser().parse(invoke(GET, url, jsonData)).getAsJsonObject();
        } catch (IOException ex) {
            return null;
        }
    }

    public static JsonObject getJsonInformation(String url) {
        try {
            return new JsonParser().parse(invoke(GET, url, null)).getAsJsonObject();
        } catch (IOException ex) {
            return null;
        }
    }

    private static String sendAndReceiveData(HttpURLConnection uc, String sendingData) throws IOException {
        if (StringUtils.isNotEmpty(sendingData)) {
            try (DataOutputStream wr = new DataOutputStream(uc.getOutputStream())) {
                wr.writeBytes(sendingData);
            }
        }
        String result = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            result = response.toString().trim();
        }

        return result;
    }

    private static HttpURLConnection createConnection(String method, String sUrl) throws IOException {
        URL url = new URL(sUrl);
        HttpURLConnection uc = null;
        
        uc = (HttpURLConnection) url.openConnection(ProxyUtil.getProxy());
        uc.setRequestMethod(method);
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setUseCaches(false);
        uc.setDoOutput(true);

        return uc;
    }

    

}
