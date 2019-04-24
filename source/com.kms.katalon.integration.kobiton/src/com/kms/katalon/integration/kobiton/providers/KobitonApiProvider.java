package com.kms.katalon.integration.kobiton.providers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.integration.kobiton.constants.KobitonStringConstants;
import com.kms.katalon.integration.kobiton.entity.KobitonApiKey;
import com.kms.katalon.integration.kobiton.entity.KobitonApplication;
import com.kms.katalon.integration.kobiton.entity.KobitonApplications;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;
import com.kms.katalon.integration.kobiton.entity.KobitonJsonDeserializer;
import com.kms.katalon.integration.kobiton.entity.KobitonLoginInfo;
import com.kms.katalon.integration.kobiton.exceptions.KobitonApiException;

public class KobitonApiProvider {
    private static final String HEADER_VALUE_AUTHORIZATION_PREFIX = "Bearer ";

    private static final String HEADER_AUTHORIZATION = "authorization";

    private static final String HEADER_VALUE_CONTENT_TYPE_JSON = "application/json";

    private static final String HEADER_CONTENT_TYPE = "Content-type";

    private static final String LOGIN_PARAM_PASSWORD = "password";

    private static final String LOGIN_PARAM_EMAIL_OR_USERNAME = "emailOrUsername";

    private static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static KobitonLoginInfo login(String username, String password)
            throws URISyntaxException, ClientProtocolException, IOException, KobitonApiException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(getKobitonURI(KobitonStringConstants.KOBITON_API_LOGIN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(LOGIN_PARAM_EMAIL_OR_USERNAME, new JsonPrimitive(username));
        jsonObject.add(LOGIN_PARAM_PASSWORD, new JsonPrimitive(password));
        httpPost.setEntity(new StringEntity(jsonObject.toString()));
        httpPost.setHeader(HEADER_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE_JSON);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        String responseString = EntityUtils.toString(httpResponse.getEntity());
        checkForApiError(responseString);
        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT_ISO_8601).create();
        return gson.fromJson(responseString, KobitonLoginInfo.class);
    }

    private static void checkForApiError(String responseString) throws KobitonApiException {
        try {
            final KobitonApiException apiException = new Gson().fromJson(responseString, KobitonApiException.class);
            if (apiException.isError()) {
                throw apiException;
            }
        } catch (JsonSyntaxException e) {
            // Not able to parse into error json, so no problem
            return;
        }
    }

    private static URI getKobitonURI(String loginPath) throws URISyntaxException {
        return new URIBuilder().setScheme(KobitonStringConstants.KOBITON_SCHEME_HTTPS)
                .setHost(KobitonStringConstants.KOBITON_HOST)
                .setPath(loginPath)
                .build();
    }

    public static List<KobitonApiKey> getApiKeyList(String token)
            throws URISyntaxException, ClientProtocolException, IOException, KobitonApiException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(getKobitonURI(KobitonStringConstants.KOBITON_API_GET_KEYS));
        setHeaderForKobitonGetRequest(token, httpGet);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        String responseString = EntityUtils.toString(httpResponse.getEntity());
        checkForApiError(responseString);
        return new Gson().fromJson(responseString, new TypeToken<List<KobitonApiKey>>() {}.getType());
    }

    public static List<KobitonDevice> getKobitonFavoriteDevices(String token)
            throws URISyntaxException, ClientProtocolException, IOException, KobitonApiException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(getKobitonURI(KobitonStringConstants.KOBITON_API_GET_ALL_DEVICES));
        setHeaderForKobitonGetRequest(token, httpGet);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        String responseString = EntityUtils.toString(httpResponse.getEntity());
        checkForApiError(responseString);
        Map<String, List<KobitonDevice>> allDevices = new GsonBuilder()
                .registerTypeAdapter(KobitonDevice.class, new KobitonJsonDeserializer())
                .create()
                .fromJson(responseString, new TypeToken<Map<String, List<KobitonDevice>>>() {}.getType());

        List<KobitonDevice> onlineAndFavouriteDevices = allDevices.getOrDefault("favoriteDevices", Collections.emptyList())
                .stream()
                .filter(d -> !d.isHidden() && d.isOnline() && d.isFavorite())
                .collect(Collectors.toList());
        Set<KobitonDevice> filteredDuplicatedDevices = new LinkedHashSet<>();
        for (KobitonDevice device : onlineAndFavouriteDevices) {
            filteredDuplicatedDevices.add(device);
        }
        return new ArrayList<>(filteredDuplicatedDevices);
    }

    public static List<KobitonApplication> getKobitionApplications(String token)
            throws URISyntaxException, ParseException, IOException, KobitonApiException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(getKobitonURI(KobitonStringConstants.KOBITON_API_GET_APPLICATION));
        setHeaderForKobitonGetRequest(token, httpGet);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        String responseString = EntityUtils.toString(httpResponse.getEntity());
        checkForApiError(responseString);
        return new Gson().fromJson(responseString, KobitonApplications.class).getApps();
    }

    private static void setHeaderForKobitonGetRequest(String token, HttpGet httpGet) {
        httpGet.setHeader(HEADER_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE_JSON);
        httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
    }
}
