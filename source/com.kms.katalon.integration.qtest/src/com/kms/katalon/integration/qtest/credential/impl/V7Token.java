package com.kms.katalon.integration.qtest.credential.impl;

import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

public class V7Token extends AbstractQTestToken {
    
    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String DF_TOKEN_TYPE = "bearer";
    
    public V7Token(String rawToken) {
        super(rawToken);
    }

    @Override
    public QTestVersion getVersion() {
        return QTestVersion.V7;
    }

    @Override
    public String getAccessTokenHeader() throws QTestInvalidFormatException {
        try {
            JsonObject js = new JsonObject(rawToken);
            if (!js.has(ACCESS_TOKEN)) {
                throw QTestInvalidFormatException.createInvalidTokenException("Missing access_token in " + rawToken);
            }
            
            if (!js.has(TOKEN_TYPE)) {
                throw QTestInvalidFormatException.createInvalidTokenException("Missing token_type in " + rawToken);
            }
            
            StringBuilder accessTokenBuilder = new StringBuilder();
            accessTokenBuilder.append(js.get(TOKEN_TYPE)).append(" ").append(js.get(ACCESS_TOKEN));
            
            return accessTokenBuilder.toString();
        } catch (JsonException e) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(e.getMessage());
        }
    }

    @Override
    public String getAccessToken() throws QTestInvalidFormatException {
        try {
            JsonObject js = new JsonObject(rawToken);
            if (!js.has(ACCESS_TOKEN)) {
                throw QTestInvalidFormatException.createInvalidTokenException("Missing access_token in " + rawToken);
            }
            
            return js.getString(ACCESS_TOKEN);
        } catch (JsonException e) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(e.getMessage());
        }
    }

}
