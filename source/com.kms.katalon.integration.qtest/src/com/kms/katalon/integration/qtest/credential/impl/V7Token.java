package com.kms.katalon.integration.qtest.credential.impl;

import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

public class V7Token extends AbstractQTestToken {
    
    public V7Token(String rawToken) {
        super(rawToken);
    }

    @Override
    public QTestVersion getVersion() {
        return QTestVersion.V7;
    }

    @Override
    public String getAccessToken() throws QTestInvalidFormatException {
        try {
            JsonObject js = new JsonObject(rawToken);
            if (!js.has("access_token")) {
                throw QTestInvalidFormatException.createInvalidJsonFormatException("Missing access_token");
            }
            
            if (!js.has("token_type")) {
                throw QTestInvalidFormatException.createInvalidJsonFormatException("Missing token_type");
            }
            
            StringBuilder accessTokenBuilder = new StringBuilder();
            accessTokenBuilder.append(js.get("token_type")).append(" ").append(js.get("access_token"));
            
            return accessTokenBuilder.toString();
        } catch (JsonException e) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(e.getMessage());
        }
    }

}
