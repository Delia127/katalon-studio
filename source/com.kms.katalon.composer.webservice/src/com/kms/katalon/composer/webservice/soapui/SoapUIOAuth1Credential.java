package com.kms.katalon.composer.webservice.soapui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class SoapUIOAuth1Credential implements SoapUICredential {
    
    private static final String AUTH_META_PREFIX = "Authorization:";
    
    private static final String AUTHORIZATION_TYPE = AUTH_META_PREFIX + "type";
    
    private static final String OAUTH_1_0 = "OAuth 1.0";
    
    private static final String AUTHORIZATION_OAUTH_CONSUMER_KEY = AUTH_META_PREFIX + "oauth_consumer_key";
    
    private static final String AUTHORIZATION_OAUTH_CONSUMER_SECRET = AUTH_META_PREFIX + "oauth_consumer_secret";
    
    private static final String AUTHORIZATION_OAUTH_SIGNATURE_METHOD = AUTH_META_PREFIX + "oauth_signature_method";
    
    private static final String AUTHORIZATION_OAUTH_TOKEN = AUTH_META_PREFIX + "oauth_token";
    
    private static final String AUTHORIZATION_OAUTH_TOKEN_SECRET = AUTH_META_PREFIX + "oauth_token_secret";
    
    private static final String HMAC_SHA1 = "HMAC-SHA1";
    
    private String profileName;
    
    private String consumerKey;
    
    private String consumerSecret;
    
    private String token;
    
    private String tokenSecret;

    public SoapUIOAuth1Credential(String profileName) {
        this.profileName = profileName;
    }
    
    protected String getProfileName() {
        return profileName;
    }

    protected String getConsumerKey() {
        return consumerKey;
    }

    protected void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    protected String getConsumerSecret() {
        return consumerSecret;
    }

    protected void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    protected String getToken() {
        return token;
    }

    protected void setToken(String token) {
        this.token = token;
    }

    protected String getTokenSecret() {
        return tokenSecret;
    }

    protected void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    @Override
    public Map<String, String> getAuthHeaders() {
        Map<String, String> authHeadersMap = new HashMap<>();
        authHeadersMap.put(AUTHORIZATION_TYPE, OAUTH_1_0);
        authHeadersMap.put(AUTHORIZATION_OAUTH_CONSUMER_KEY, StringUtils.defaultIfBlank(consumerKey, ""));
        authHeadersMap.put(AUTHORIZATION_OAUTH_CONSUMER_SECRET, StringUtils.defaultIfBlank(consumerSecret, ""));
        authHeadersMap.put(AUTHORIZATION_OAUTH_SIGNATURE_METHOD, HMAC_SHA1);
        if (StringUtils.isNotBlank(token)) {
            authHeadersMap.put(AUTHORIZATION_OAUTH_TOKEN, token);
        }
        if (StringUtils.isNotBlank(tokenSecret)) {
            authHeadersMap.put(AUTHORIZATION_OAUTH_TOKEN_SECRET, tokenSecret);
        }
        return authHeadersMap;
    }
    
   
}
