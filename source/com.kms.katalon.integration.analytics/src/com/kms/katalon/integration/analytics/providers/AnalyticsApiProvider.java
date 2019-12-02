package com.kms.katalon.integration.analytics.providers;

import static com.kms.katalon.core.network.HttpClientProxyBuilder.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.network.HttpClientProxyBuilder;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsApiKey;
import com.kms.katalon.integration.analytics.entity.AnalyticsExecution;
import com.kms.katalon.integration.analytics.entity.AnalyticsFeature;
import com.kms.katalon.integration.analytics.entity.AnalyticsFileInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsLicenseKey;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganizationPage;
import com.kms.katalon.integration.analytics.entity.AnalyticsPage;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsProjectPage;
import com.kms.katalon.integration.analytics.entity.AnalyticsRunConfiguration;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeamPage;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestRun;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsTracking;
import com.kms.katalon.integration.analytics.entity.AnalyticsUploadInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsUser;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.logging.LogUtil;

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

    public static boolean testConnection(String serverUrl) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_PUBLIC_INFO);
            URIBuilder uriBuilder = new URIBuilder(uri);
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            executeRequest(httpGet, Object.class);
            return true;
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static AnalyticsTokenInfo requestToken(String serverUrl, String email, String password)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_TOKEN);
            URIBuilder uriBuilder = new URIBuilder(uri);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(LOGIN_PARAM_USERNAME, email));
            nameValuePairs.add(new BasicNameValuePair(LOGIN_PARAM_PASSWORD, password));
            nameValuePairs.add(new BasicNameValuePair(LOGIN_PARAM_GRANT_TYPE_NAME, LOGIN_PARAM_GRANT_TYPE_VALUE));

            
            HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs, Charsets.UTF_8);
            HttpPost httpPost = new HttpPost(uriBuilder.build().toASCIIString());
            httpPost.setEntity(entity);
            String clientCredentials = OAUTH2_CLIENT_ID + ":" + OAUTH2_CLIENT_SECRET;
            httpPost.setHeader(HEADER_AUTHORIZATION,
                    HEADER_AUTHORIZATION_PREFIX + Base64.getEncoder().encodeToString(clientCredentials.getBytes()));

            return executeRequest(httpPost, AnalyticsTokenInfo.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static AnalyticsOrganization getOrganization(String serverUrl, String accessToken, long orgId) throws AnalyticsApiExeception {
        try {
            URIBuilder uriBuilder = new URIBuilder(serverUrl);
            uriBuilder.setPath(String.format(AnalyticsStringConstants.ANALYTICS_API_ORGANIZATION_ITEM, orgId));
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            AnalyticsOrganization organization = executeRequest(httpGet, AnalyticsOrganization.class);
            return organization;
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static List<AnalyticsOrganization> getOrganizations(String serverUrl, String accessToken) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_USERS_ME);
            URIBuilder uriBuilder = new URIBuilder(uri);
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            AnalyticsOrganizationPage organizationPage = executeRequest(httpGet, AnalyticsOrganizationPage.class);
            return organizationPage.getOrganizations();
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static List<AnalyticsTeam> getTeams(String serverUrl, String accessToken, Long orgId) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_USERS_ME);
            URIBuilder uriBuilder = new URIBuilder(uri);
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            AnalyticsTeamPage teamPage = executeRequest(httpGet, AnalyticsTeamPage.class);
            
            List<AnalyticsTeam> teams = new ArrayList<>();
            for (AnalyticsTeam team : teamPage.getTeams()) {
                if (team.getOrganization().getId().equals(orgId)) {
                    teams.add(team);
                }
            }
            return teams;
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    public static List<AnalyticsApiKey> getApiKeys(String serverUrl, String accessToken) throws Exception {
        URI uri = getApiURI(serverUrl, "/api/v1/search");
        URIBuilder builder = new URIBuilder(uri);
        String query = 
        "{"
        + "\"type\":\"ApiKey\","
        + "\"conditions\":[],"
        + "\"functions\":[],"
        + "\"pagination\":{\"page\":0,\"size\":1,\"sorts\":[\"createdAt,asc\"]},"
        + "\"groupBys\":[]"
        + "}";
        builder.setParameter("q", query);
        
        HttpGet httpGet = new HttpGet(builder.build().toASCIIString());
        httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
        AnalyticsPage<AnalyticsApiKey> result =  executeRequest(httpGet, new TypeToken<AnalyticsPage<AnalyticsApiKey>>() {});
        return result.getContent();
        
    }

    public static List<AnalyticsProject> getProjects(String serverUrl, AnalyticsTeam team, String accessToken)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_PROJECTS);
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (team != null && team.getId() != 0) {
                uriBuilder.setParameter("teamId", team.getId() + "");
                uriBuilder.setParameter("sort", "name,asc");
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            AnalyticsProjectPage projectPage = executeRequest(httpGet, AnalyticsProjectPage.class);
            return projectPage.getContent();
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static AnalyticsProject createProject(String serverUrl, String projectName, AnalyticsTeam team,
            String accessToken) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_PROJECTS);
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

            return executeRequest(httpPost, AnalyticsProject.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    public static AnalyticsLicenseKey getLicenseKey(String serverUrl, String username, String sessionId,
            String hostname, String machineKey, String accessToken) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_ACTIVATE);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("machineKey", machineKey + "");
            uriBuilder.setParameter("ksVersion", VersionUtil.getCurrentVersion().getVersion());
            uriBuilder.setParameter("email", username);
            uriBuilder.setParameter("sessionId", sessionId);
            uriBuilder.setParameter("hostname", hostname);
            
            KatalonPackage katalonPackage = KatalonApplication.getKatalonPackage();
            if (KatalonPackage.ENGINE.equals(katalonPackage)) {
                String isFloatingEngine = System.getenv("ECLIPSE_SANDBOX");
                if ("1.11".equals(isFloatingEngine)) {
                    katalonPackage = KatalonPackage.FLOATING_ENGINE;
                }
            }
            uriBuilder.setParameter("package", katalonPackage.getPackageName());
            
            HttpPost httpPost = new HttpPost(uriBuilder.build().toASCIIString());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            AnalyticsLicenseKey licenseKey = executeRequest(httpPost, AnalyticsLicenseKey.class);
            return licenseKey;
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    public static void releaseLicense(String serverUrl, String machineId, String ksVersion, String sessionId,
            Long orgId, String accessToken) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_RELEASE_LICENSE);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("machineKey", machineId);
            uriBuilder.setParameter("ksVersion", ksVersion);
            uriBuilder.setParameter("sessionId", sessionId);
            uriBuilder.setParameter("package", KatalonApplication.getKatalonPackage().getPackageName());
            if (orgId != null) {
                uriBuilder.setParameter("organizationId", String.valueOf(orgId));
            }
            HttpPost httpPost = new HttpPost(uriBuilder.build().toASCIIString());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            executeRequest(httpPost, Object.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static void sendTrackingActivity(String serverUrl, String accessToken, AnalyticsTracking trackingInfo) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_TRACKING_ACTIVITY);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            StringEntity entity = new StringEntity(gson.toJson(trackingInfo));
            httpPost.setEntity(entity);

            executeRequest(httpPost, true, Object.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static List<AnalyticsExecution> sendLog(String serverUrl, long projectId, long timestamp, String folderName, File file,
            boolean isEnd, String token) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_KATALON_TEST_REPORTS);
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

            return executeRequest(httpPost, new TypeToken<ArrayList<AnalyticsExecution>>() {});
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static AnalyticsUploadInfo getUploadInfo(String serverUrl, String token, long projectId)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_UPLOAD_URL);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            return executeRequest(httpGet, AnalyticsUploadInfo.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static List<AnalyticsUploadInfo> getMultipleUploadInfo(String serverUrl, String token, long projectId,
            long numberUploadInfo) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_UPLOAD_URLS);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));
            uriBuilder.setParameter("numberUrl", String.valueOf(numberUploadInfo));
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            return executeRequest(httpGet, new TypeToken<ArrayList<AnalyticsUploadInfo>>() {});
        } catch (Exception e) {
            LogUtil.logError(e);
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static void uploadFile(String url, File file) throws AnalyticsApiExeception {
        try (InputStream content = new FileInputStream(file)) {
            HttpEntity entity = new InputStreamEntity(content, file.length());
            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(entity);
            executeRequest(httpPut, Object.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    public static List<AnalyticsFeature> getFeatures(String serverUrl, String accessToken, long organizationId, String ksVersion) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_FEATURES_URL);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("organizationId", String.valueOf(organizationId));
            uriBuilder.setParameter("ksVersion", ksVersion);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + accessToken);
            List<AnalyticsFeature> features = executeRequest(httpGet, new TypeToken<ArrayList<AnalyticsFeature>>() {});
            return features;
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static List<AnalyticsExecution> uploadFileInfo(String serverUrl, long projectId, long timestamp, String folderName,
            String fileName, String uploadedPath, boolean isEnd, String token) throws AnalyticsApiExeception {

        try {
            LogUtil.logInfo("Katalon TestOps: Start uploading report to Katalon TestOps server: " + serverUrl);
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_KATALON_TEST_REPORTS);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));
            uriBuilder.setParameter("batch", String.valueOf(timestamp));
            uriBuilder.setParameter("folderPath", folderName);
            uriBuilder.setParameter("isEnd", String.valueOf(isEnd));
            uriBuilder.setParameter("fileName", fileName);
            uriBuilder.setParameter("uploadedPath", uploadedPath);

            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);

            return executeRequest(httpPost, new TypeToken<ArrayList<AnalyticsExecution>>() {});
        } catch (Exception e) {
            LogUtil.logError(e);
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static List<AnalyticsExecution> uploadMultipleFileInfo(String serverUrl, long projectId, long timestamp,
            List<AnalyticsFileInfo> fileInfoList, String token) throws AnalyticsApiExeception {
        try {
            LogUtil.logInfo("Katalon TestOps: Start uploading report to Katalon TestOps server: " + serverUrl);
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_KATALON_MULTIPLE_TEST_REPORTS);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));
            uriBuilder.setParameter("batch", String.valueOf(timestamp));

            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Gson gson = new GsonBuilder().create();
            StringEntity entity = new StringEntity(gson.toJson(fileInfoList));
            httpPost.setEntity(entity);

            return executeRequest(httpPost, new TypeToken<ArrayList<AnalyticsExecution>>() {});
        } catch (Exception e) {
            LogUtil.logError(e);
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    public static AnalyticsTestProject uploadTestProject(String serverUrl, long projectId, long teamId, long timestamp, String name, String folderName,
            String fileName, String uploadedPath, String token) throws AnalyticsApiExeception {

        try {
            LogUtil.logInfo("Katalon TestOps: Start uploading test project to Katalon TestOps server: " + serverUrl);
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_UPLOAD_TEST_PROJECT);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("name", name);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));
            uriBuilder.setParameter("teamId", String.valueOf(teamId));
            uriBuilder.setParameter("batch", String.valueOf(timestamp));
            uriBuilder.setParameter("folderPath", folderName);
            uriBuilder.setParameter("fileName", fileName);
            uriBuilder.setParameter("uploadedPath", uploadedPath);

            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);

            return executeRequest(httpPost, AnalyticsTestProject.class);
        } catch (Exception e) {
            LogUtil.logError(e);
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static AnalyticsRunConfiguration createTestPlan(String serverUrl, long projectId, long teamId, String name, long testProjectId,
            String cloudType, String configType, long testSuiteCollectionId, String token)
            throws AnalyticsApiExeception {
        try {
            LogUtil.logInfo("Katalon TestOps: Create test plan in Katalon TestOps server: " + serverUrl);
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_CREATE_TEST_PLAN);
            URIBuilder uriBuilder = new URIBuilder(uri);

            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("projectId", String.valueOf(projectId));
            map.put("teamId", String.valueOf(teamId));
            map.put("testProjectId", String.valueOf(testProjectId));
            map.put("cloudType", cloudType);
            map.put("configType", configType);
            map.put("testSuiteCollectionId", String.valueOf(testSuiteCollectionId));

            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(map));
            httpPost.setEntity(entity);

            return executeRequest(httpPost, AnalyticsRunConfiguration.class);
        } catch (Exception e) {
            LogUtil.logError(e);
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    private static String executeRequest(HttpUriRequest httpRequest, boolean isSilent) throws Exception {
        HttpClientProxyBuilder httpClientProxyBuilder = create(ProxyPreferences.getProxyInformation(), httpRequest.getURI().getHost());
        HttpClient httpClient = httpClientProxyBuilder.getClientBuilder().build();
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        HttpEntity entity = httpResponse.getEntity();
        String responseString = StringUtils.EMPTY;
        if (entity != null) {
            responseString = EntityUtils.toString(httpResponse.getEntity());
        }

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_NO_CONTENT) {
            return responseString;
        }

        if (!isSilent) {
            LogUtil.logError(MessageFormat.format(
                    "Katalon TestOps: Unexpected response, URL: {0}, Status: {1}, Response: {2}",
                    httpRequest.getURI().toString(), statusCode, responseString));
        }

        throw new AnalyticsApiExeception(responseString);
    }

    private static <T> T executeRequest(HttpUriRequest httpRequest, boolean isSilent, Class<T> returnType) throws Exception {
        String responseString = executeRequest(httpRequest, isSilent);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(responseString, returnType);
    }

    private static <T> T executeRequest(HttpUriRequest httpRequest, Class<T> returnType) throws Exception {
        String responseString = executeRequest(httpRequest, false);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(responseString, returnType);
    }

    private static <T> T executeRequest(HttpUriRequest httpRequest, boolean isSilent, TypeToken<T> typeToken) throws Exception {
        String responseString = executeRequest(httpRequest, isSilent);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(responseString, typeToken.getType());
    }

    private static <T> T executeRequest(HttpUriRequest httpRequest, TypeToken<T> typeToken) throws Exception {
        String responseString = executeRequest(httpRequest, false);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(responseString, typeToken.getType());
    }

    private static URI getApiURI(String host, String path) throws URISyntaxException {
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        return new URIBuilder().setPath(host + path).build();
    }

    public static void updateTestRunResult(String serverUrl, long projectId, String token, AnalyticsTestRun testRun)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_KATALON_TEST_RUN_RESULT);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("projectId", String.valueOf(projectId));

            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Gson gson = new GsonBuilder().create();
            StringEntity entity = new StringEntity(gson.toJson(testRun));
            httpPost.setEntity(entity);
            executeRequest(httpPost, Object.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    public static AnalyticsOrganization createDefaultOrganization(String serverUrl, String token)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_ORGANIZATION);
            URIBuilder uriBuilder = new URIBuilder(uri);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return executeRequest(httpPost, AnalyticsOrganization.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }
    
    public static void deactivate(String serverUrl, String token, String machineId, Long orgId)
            throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_DEACTIVATE);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("machineKey", machineId);
            uriBuilder.setParameter("package", KatalonApplication.getKatalonPackage().getPackageName());
            if (orgId != null) {
                uriBuilder.setParameter("organizationId", String.valueOf(orgId));
            }
            HttpDelete httpPost = new HttpDelete(uriBuilder.build().toASCIIString());
            httpPost.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            executeRequest(httpPost, Object.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static Date getExpirationOnline(String serverUrl, String token, Long orgId) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_API_LICENSE_EXPIRATION);
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("feature", KatalonApplication.getKatalonPackage().getPackageName());
            if (orgId != null) {
                uriBuilder.setParameter("organizationId", String.valueOf(orgId));
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            return executeRequest(httpGet, Date.class);
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }

    public static Date getExpirationTrial(String serverUrl, String token) throws AnalyticsApiExeception {
        try {
            URI uri = getApiURI(serverUrl, AnalyticsStringConstants.ANALYTICS_USERS_ME);
            URIBuilder uriBuilder = new URIBuilder(uri);
            HttpGet httpGet = new HttpGet(uriBuilder.build().toASCIIString());
            httpGet.setHeader(HEADER_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION_PREFIX + token);
            AnalyticsUser user = executeRequest(httpGet, AnalyticsUser.class);
            return user.getTrialExpirationDate();
        } catch (Exception e) {
            throw AnalyticsApiExeception.wrap(e);
        }
    }
}
