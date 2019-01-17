package com.kms.katalon.plugin.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.program.Program;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.network.HttpClientProxyBuilder;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreToken;
import com.kms.katalon.plugin.util.KStoreTokenService;

public class KStoreRestClient {
    
    private KStoreCredentials credentials;
    
    public KStoreRestClient(KStoreCredentials credentials) {
        this.credentials = credentials;
    }
    
    public List<KStorePlugin> getLatestPlugins() throws KStoreClientException {
        AtomicReference<List<KStorePlugin>> plugins = new AtomicReference<>();
        try {
            executeGetRequest(getPluginsAPIUrl(), credentials, response -> {
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseContent = EntityUtils.toString(response.getEntity());
                        plugins.set(parsePluginListJson(responseContent));
                    } else {
                        throw new KStoreClientException("Failed to get latest plugin. No content returned from server.");
                    }
                } catch (Exception e) {
                    propagateIfInstanceOf(e, KStoreClientException.class);
                    throw new KStoreClientException("Unexpected error occurs during executing get latest plugins", e);
                }
            });
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during executing get latest plugins", e);
        }
        return plugins.get();
    }
    
    private List<KStorePlugin> parsePluginListJson(String json) 
            throws ParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Type listType = new TypeToken<List<KStorePlugin>>() {}.getType();
        return gson.fromJson(json, listType);
    }
    
    public void downloadPlugin(long productId, File downloadFile) throws KStoreClientException {
        try {
            executeGetRequest(getPluginDownloadAPIUrl(productId), credentials, response -> {
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        FileOutputStream outstream = new FileOutputStream(downloadFile);
                        entity.writeTo(outstream);
                        outstream.close();
                    } else {
                        throw new KStoreClientException("Failed to download plugin. No content returned from server.");
                    }
                } catch (Exception e) {
                    propagateIfInstanceOf(e, KStoreClientException.class);
                    throw new KStoreClientException("Unexpected error occurs during executing download plugin", e);
                }
            });
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during executing download plugin", e);
        }
    }

    public AuthenticationResult authenticate() throws KStoreClientException {
        try {
            HttpPost post = new HttpPost(getAuthenticateAPIUrl());
            addAuthenticationHeaders(credentials, post);
            
            String content = JsonUtil.toJson(credentials);
            StringEntity requestEntity = new StringEntity(content);
            post.setEntity(requestEntity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            
            try (CloseableHttpClient client = getHttpClient();
                CloseableHttpResponse response = client.execute(post);) {
                int statusCode = response.getStatusLine().getStatusCode();
                AuthenticationResult result = new AuthenticationResult();
                if (statusCode == HttpStatus.SC_OK) {
                    result.setAuthenticated(true);
                    HttpEntity responseEntity = response.getEntity();
                    if (responseEntity != null) { // just in case
                        String token = EntityUtils.toString(responseEntity);
                        result.setToken(token);
                    }
                    return result;
                } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    result.setAuthenticated(false);
                    result.setToken(StringUtils.EMPTY);
                    return result;
                } else {
                    throw new KStoreClientException(String.format(
                            "Invalid Request. Status Code: %d. Message: %s",
                            response.getStatusLine().getStatusCode(),
                            response.getStatusLine().getReasonPhrase()));
                }
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during executing authenticate", e);
        }
    }

    public void goToSearchPluginPage() throws KStoreClientException {
        try {
            KStoreToken token = getToken();
            if (token != null) {
                String searchPluginUrl = getSearchPluginUrl(token.getToken());
                Program.launch(searchPluginUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Search Plugins page", e);
        }
    }
    
    public void goToManagePluginsPage() throws KStoreClientException {
        try {
            KStoreToken token = getToken();
            if (token != null) {
                String managePluginsUrl = getManagePluginUrl(token.getToken());
                Program.launch(managePluginsUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Manage Plugins page", e);
        }
    }
    
    private KStoreToken getToken() throws IOException, KStoreClientException {
        KStoreToken token = KStoreTokenService.getInstance().getToken();
        if (KStoreTokenService.getInstance().isTokenExpired(token)) {
            AuthenticationResult authenticateResult = authenticate();
            if (authenticateResult.isAuthenticated()) {
                String tokenString = authenticateResult.getToken();
                token = KStoreTokenService.getInstance().createNewToken(tokenString);
            } else {
                token = null;
            }
        }
        return token;
    }
    
    private void executeGetRequest(
            String url,
            KStoreCredentials credentials,
            OnRequestSuccessHandler requestSuccessHandler) 
                    throws URISyntaxException, IOException, GeneralSecurityException, KStoreClientException {
        
        HttpGet get = new HttpGet(url);
        addAuthenticationHeaders(credentials, get);
        CloseableHttpClient client = getHttpClient();
        CloseableHttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            requestSuccessHandler.handleRequestSuccess(response);
        } else {
            throw new KStoreClientException(String.format("Invalid Request. Status Code: %d. Message: %s",
                    response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
        }
        IOUtils.closeQuietly(client);
        IOUtils.closeQuietly(response);
    }
    
    private <X extends Throwable> void propagateIfInstanceOf(Throwable throwable, Class<X> declaredType) throws X {
        if (throwable != null && declaredType.isInstance(throwable)) {
            throw declaredType.cast(throwable);
        }
    }
    
    private CloseableHttpClient getHttpClient() throws URISyntaxException, IOException, GeneralSecurityException {
        return HttpClientProxyBuilder.create(ProxyPreferences.getProxyInformation()).getClientBuilder().build();
    }
    
    private void addAuthenticationHeaders(KStoreCredentials credentials, HttpRequestBase request) {
        credentials.getAuthHeaders().entrySet().stream()
            .forEach(entry -> request.addHeader(entry.getKey(), entry.getValue()));
    }
    
    private String getSearchPluginUrl(String token) {
        return getKatalonStoreUrl() + "?token=" + token;
    }
    
    private String getManagePluginUrl(String token) {
        return getKatalonStoreUrl() + "/manage/products?token=" + token;
    }
    
    private String getAuthenticateAPIUrl() {
        return getKatalonStoreAPIUrl() + "/authenticate";
    }
    
    private String getPluginsAPIUrl() {
        return getKatalonStoreAPIUrl() + "/products/ks";
    }
    
    private String getPluginDownloadAPIUrl(long pluginId) {
        return getKatalonStoreAPIUrl() + "/download/source/" + pluginId;
    }
    
    private String getKatalonStoreAPIUrl() {
        return getKatalonStoreUrl() + "/api";
    }
    
    private String getKatalonStoreUrl() {
        return "https://store-staging.katalon.com";
    }
    
    private interface OnRequestSuccessHandler {
        void handleRequestSuccess(CloseableHttpResponse response) throws KStoreClientException;
    }
    
    public class AuthenticationResult {
        private boolean isAuthenticated;
        
        private String token;

        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        public void setAuthenticated(boolean isAuthenticated) {
            this.isAuthenticated = isAuthenticated;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
