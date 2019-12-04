package com.kms.katalon.plugin.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.network.HttpClientProxyBuilder;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreClientExceptionWithInfo;
import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreProduct;
import com.kms.katalon.plugin.models.KStoreProductID;
import com.kms.katalon.plugin.models.KatalonStoreToken;
import com.kms.katalon.plugin.util.KStoreTokenService;
import com.kms.katalon.plugin.util.KStoreUrls;

public class KStoreRestClient {
    private KStoreCredentials credentials;
    
    public KStoreRestClient(KStoreCredentials credentials) {
        this.credentials = credentials;
    }
    
    public List<KStorePlugin> getLatestPlugins(String appVersion, KatalonPackage katalonPackage, LicenseType licenseType) throws KStoreClientExceptionWithInfo {
        AtomicReference<List<KStorePlugin>> plugins = new AtomicReference<>();
        String url = KStoreUrls.getPluginsAPIUrl(appVersion, katalonPackage, licenseType);
        try {
            executeGetRequest(url, credentials, response -> {
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseContent = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                        LogUtil.writeOutputLine("Latest plugins responses: " + responseContent);
                        responseContent = responseContent.replace("{}", "null");
                        LogService.getInstance().logInfo("Katalon version: " + appVersion);
                        LogService.getInstance().logInfo("Plugin info URL: " + url);
                        plugins.set(parsePluginListJson(responseContent));
                    } else {
                        throw new KStoreClientException(
                                "Failed to get latest plugin. No content returned from server.");
                    }
                } catch (Exception e) {
                    propagateIfInstanceOf(e, KStoreClientException.class);
                    throw new KStoreClientException("Unexpected error occurs during executing get latest plugins", e);
                }
            });
        } catch (Exception e) {
            if (StringUtils.containsIgnoreCase(e.getMessage(), StringConstants.KStore_ERROR_INVALID_CREDENTAILS)) {
                throw new KStoreClientExceptionWithInfo(e.getMessage(), credentials, url);
            }
            propagateIfInstanceOf(e, KStoreClientExceptionWithInfo.class);
            throw new KStoreClientExceptionWithInfo("Unexpected error occurs during executing get latest plugins",
                    credentials, url, e);
        }
        return plugins.get();
    }
    
    public List<KStoreProduct> getRecommendPlugins() throws KStoreClientException {
        AtomicReference<List<KStoreProduct>> products = new AtomicReference<>();
        try {
            executeGetRequest(KStoreUrls.getRecommendedPluginsAPIUrl(), credentials, response -> {
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseContent = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                        //LogService.getInstance().logInfo("Latest plugins responses: " + responseContent);
                        responseContent = responseContent.replace("{}", "null");
                        //LogService.getInstance().logInfo("Katalon version: " + appVersion);
                        //LogService.getInstance().logInfo("Plugin info URL: " + getKSRecommendPlugins());
                        products.set(parseProductListJson(responseContent));
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
        return products.get() ;
    }
    private List<KStorePlugin> parsePluginListJson(String json) 
            throws ParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Type listType = new TypeToken<List<KStorePlugin>>() {}.getType();
        return gson.fromJson(json, listType);
    }
    
    private List<KStoreProduct> parseProductListJson(String json) 
            throws ParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Type listType = new TypeToken<List<KStoreProduct>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    public void downloadPlugin(KStorePlugin plugin, File downloadFile) throws KStoreClientException {
        try {
            executeGetRequest(KStoreUrls.getPluginDownloadAPIUrl(plugin), credentials, response -> {
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
            HttpPost post = new HttpPost(KStoreUrls.getAuthenticateAPIUrl());
            addAuthenticationHeaders(credentials, post);
            
            String content = JsonUtil.toJson(credentials);
            StringEntity requestEntity = new StringEntity(content, StandardCharsets.UTF_8.name());
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
                        String token = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
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
    
    public void postRecommended(List<Long> productsID) throws KStoreClientException {
        try {
            HttpPost post = new HttpPost(KStoreUrls.getInstallRecommendedPluginsAPIUrl());
            addAuthenticationHeaders(credentials, post);
            KStoreProductID ks = new KStoreProductID(productsID);
            String content = JsonUtil.toJson(ks);
            StringEntity requestEntity = new StringEntity(content);
            post.setEntity(requestEntity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            try (CloseableHttpClient client = getHttpClient(); CloseableHttpResponse response = client.execute(post);) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    LoggerSingleton.logInfo("Successful");
                } else {
                    throw new KStoreClientException(String.format("Invalid Request. Status Code: %d. Message: %s",
                            response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
                }
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during executing authenticate", e);
        }

    }

    public void goToSearchPluginPage() throws KStoreClientException {
        try {
            KatalonStoreToken token = getToken();
            if (token != null) {
                String searchPluginPageUrl = KStoreUrls.getSearchPluginPageUrl(token.getToken());
                Program.launch(searchPluginPageUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Search Plugins page", e);
        }
    }
    
    public void goToManagePluginsPage() throws KStoreClientException {
        try {
            KatalonStoreToken token = getToken();
            if (token != null) {
                String managePluginsPageUrl = KStoreUrls.getManagePluginPageUrl(token.getToken());
                Program.launch(managePluginsPageUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Manage Plugins page", e);
        }
    }
    
    public void goToManageApiKeysPage() throws KStoreClientException {
        try {
            KatalonStoreToken token = getToken();
            if (token != null) {
                String manageApiKeysPageUrl = KStoreUrls.getManageApiKeysPageUrl(token.getToken());
                Program.launch(manageApiKeysPageUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Manage API Keys page", e);
        }
    }
    
    public void goToProductPage(KStoreProduct product) throws KStoreClientException {
        try {
            KatalonStoreToken token = getToken();
            if (token != null) {
                String productPageUrl = KStoreUrls.getProductPageUrl(product, token.getToken());
                Program.launch(productPageUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Manage Plugins page", e);
        }
    }
    
    public void goToProductReviewPage(KStoreProduct product) throws KStoreClientException {
        try {
            KatalonStoreToken token = getToken();
            if (token != null) {
                String productReviewPageUrl = KStoreUrls.getProductReviewPageUrl(product, token.getToken());
                Program.launch(productReviewPageUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Manage Plugins page", e);
        }
    }
    
    public void goToProductPricingPage(KStoreProduct product) throws KStoreClientException {
        try {
            KatalonStoreToken token = getToken();
            if (token != null) {
                String productPricingPageUrl = KStoreUrls.getProductPricingPageUrl(product, token.getToken());
                Program.launch(productPricingPageUrl);
            }
        } catch (Exception e) {
            propagateIfInstanceOf(e, KStoreClientException.class);
            throw new KStoreClientException("Unexpected error occurs during opening Manage Plugins page", e);
        }
    }
    
    private KatalonStoreToken getToken() throws IOException, KStoreClientException, GeneralSecurityException {
        KatalonStoreToken token = KStoreTokenService.getInstance().getToken();
        if (token == null || KStoreTokenService.getInstance().isTokenExpired(token)) {
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
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            requestSuccessHandler.handleRequestSuccess(response);
        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new KStoreClientException(StringConstants.KStore_ERROR_INVALID_CREDENTAILS);
        } else {
            throw new KStoreClientException(String.format("Invalid Request. Status Code: %d. Message: %s",
                    statusCode, response.getStatusLine().getReasonPhrase()));
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
