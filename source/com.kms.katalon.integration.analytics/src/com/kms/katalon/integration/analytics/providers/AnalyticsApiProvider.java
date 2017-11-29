package com.kms.katalon.integration.analytics.providers;

import static com.kms.katalon.integration.analytics.providers.HttpClientProxyBuilder.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsProjectPage;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeamPage;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsUploadInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;

public class AnalyticsApiProvider {

    private static final String HEADER_VALUE_AUTHORIZATION_PREFIX = "Bearer ";

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private static final String HEADER_AUTHORIZATION_PREFIX = "Basic ";

    private static final String LOGIN_PARAM_PASSWORD = "password";

    private static final String LOGIN_PARAM_USERNAME = "username";

    private static final String LOGIN_PARAM_GRANT_TYPE_NAME = "grant_type";

    private static final String LOGIN_PARAM_GRANT_TYPE_VALUE = "password";

    private static final String OAUTH2_CLIENT_ID = "kit_uploader";

    private static final String OAUTH2_CLIENT_SECRET = "kit_uploader";

    public static AnalyticsTokenInfo requestToken(String serverUrl, String email, String password)
            throws AnalyticsApiExeception {
        try {
            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            CloseableHttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();

            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_TOKEN);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter(LOGIN_PARAM_USERNAME, email);
            uriBuilder.setParameter(LOGIN_PARAM_PASSWORD, password);
            uriBuilder.setParameter(LOGIN_PARAM_GRANT_TYPE_NAME, LOGIN_PARAM_GRANT_TYPE_VALUE);

            HttpPost httpPost = new HttpPost(uriBuilder.build().toASCIIString());
            String clientCredentials = OAUTH2_CLIENT_ID + ":" + OAUTH2_CLIENT_SECRET;
            httpPost.setHeader(HEADER_AUTHORIZATION,
                    HEADER_AUTHORIZATION_PREFIX + Base64.getEncoder().encodeToString(clientCredentials.getBytes()));
            HttpResponse httpResponse = httpClient.execute(httpPost, httpClientProxyBuilder.getClientContext());
            String responseString = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new GsonBuilder().create();
            AnalyticsTokenInfo tokenInfo = gson.fromJson(responseString, AnalyticsTokenInfo.class);
            if (tokenInfo == null || StringUtils.isBlank(tokenInfo.getAccess_token())) {
                throw new AnalyticsApiExeception(new Throwable(IntegrationAnalyticsMessages.MSG_REQUEST_TOKEN_ERROR));
            }
            return tokenInfo;
        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    public static List<AnalyticsTeam> getTeams(String serverUrl, String accessToken) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_TEAMS);
            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("sort", "name,asc");
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            HttpResponse httpResponse = httpClient.execute(httpGet, httpClientProxyBuilder.getClientContext());
            String responseString = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new GsonBuilder().create();
            AnalyticsTeamPage teamPage = gson.fromJson(responseString, AnalyticsTeamPage.class);
            return teamPage.getContent();
        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    public static List<AnalyticsProject> getProjects(String serverUrl, AnalyticsTeam team, String accessToken)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_PROJECTS);
            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (team != null && team.getId() != 0) {
                uriBuilder.setParameter("teamId", team.getId() + "");
                uriBuilder.setParameter("sort", "name,asc");
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            HttpResponse httpResponse = httpClient.execute(httpGet, httpClientProxyBuilder.getClientContext());
            String responseString = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new GsonBuilder().create();
            AnalyticsProjectPage projectPage = gson.fromJson(responseString, AnalyticsProjectPage.class);
            return projectPage.getContent();
        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    public static AnalyticsProject createProject(String serverUrl, String projectName, AnalyticsTeam team,
            String accessToken) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_PROJECTS);
            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);

            Map<String, String> map = new HashMap<>();
            map.put("name", projectName);

            if (team != null && team.getId() != null) {
                map.put("teamId", team.getId() + "");
            }

            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(map));
            httpPost.setEntity(entity);

            HttpResponse httpResponse = httpClient.execute(httpPost, httpClientProxyBuilder.getClientContext());
            String responseString = EntityUtils.toString(httpResponse.getEntity());
            return gson.fromJson(responseString, AnalyticsProject.class);
        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    public static void sendLog(String serverUrl, long projectId, long timestamp, String folderName, File file,
            boolean isEnd, String token) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_KATALON_TEST_REPORTS);
            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);

            StringBody projectIdPart = new StringBody(projectId + "", ContentType.MULTIPART_FORM_DATA);
            StringBody batchPart = new StringBody(timestamp + "", ContentType.MULTIPART_FORM_DATA);
            StringBody isEndPart = new StringBody(isEnd + "", ContentType.MULTIPART_FORM_DATA);
            StringBody folderPathPart = new StringBody(folderName, ContentType.MULTIPART_FORM_DATA);
            FileBody fileBodyPart = new FileBody(file, ContentType.DEFAULT_BINARY);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("projectId", projectIdPart);
            builder.addPart("batch", batchPart);
            builder.addPart("folderPath", folderPathPart);
            builder.addPart("isEnd", isEndPart);
            builder.addPart("file", fileBodyPart);

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            httpClient.execute(httpPost, httpClientProxyBuilder.getClientContext());

        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    public static AnalyticsUploadInfo getUploadInfo(String serverUrl, String token, long projectId)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_UPLOAD_URL);
            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            HttpResponse httpResponse = httpClient.execute(httpGet, httpClientProxyBuilder.getClientContext());
            String responseString = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(responseString, AnalyticsUploadInfo.class);
        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    public static void uploadFile(String url, File file) throws AnalyticsApiExeception {
        try (InputStream content = new FileInputStream(file)) {
            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
            HttpEntity entity = new InputStreamEntity(content, file.length());
            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(entity);
            httpClient.execute(httpPut, httpClientProxyBuilder.getClientContext());
        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    public static void uploadFileInfo(String serverUrl, long projectId, long timestamp, String folderName,
            String fileName, String uploadedPath, boolean isEnd, String token) throws AnalyticsApiExeception {

        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_KATALON_TEST_REPORTS);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));
            uriBuilder.setParameter("batch", String.valueOf(timestamp));
            uriBuilder.setParameter("folderPath", folderName);
            uriBuilder.setParameter("isEnd", String.valueOf(isEnd));
            uriBuilder.setParameter("fileName", fileName);
            uriBuilder.setParameter("uploadedPath", uploadedPath);

            HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation());
            HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);

            httpClient.execute(httpPost, httpClientProxyBuilder.getClientContext());
        } catch (Exception e) {
            throw new AnalyticsApiExeception(e);
        }
    }

    private static URI getApiURI(String host, String path) throws URISyntaxException {
        return new URIBuilder().setPath(host + path).build();
    }

}
