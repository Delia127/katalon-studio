package com.kms.katalon.integration.qtest.credential;

import org.apache.commons.lang.StringUtils;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.integration.qtest.credential.impl.V6Token;
import com.kms.katalon.integration.qtest.credential.impl.V7Token;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestVersion;


public class QTestTokenManager {    
    public static IQTestToken getToken(String rawToken) throws QTestInvalidFormatException {
        if (StringUtils.isBlank(rawToken)) {
            return null;
        }
        IQTestToken token = null;
        try {
            new JsonObject(rawToken);
            token = new V7Token(rawToken);
        } catch (JsonException ex) {
            token = new V6Token(rawToken);
        }
        
        if (token != null) {
            token.getAccessTokenHeader();
        }
        return token;
    }
    
    public static IQTestToken getToken(QTestVersion version, String rawToken) throws QTestInvalidFormatException {
        IQTestToken qTestToken = getToken(rawToken);
        
        if (qTestToken.getVersion().higherThan(version)) {
            throw new QTestInvalidFormatException("Token and version are not compatible");
        } else {
            return qTestToken;
        }
    }
    
    public static IQTestToken getTokenByAccessToken(QTestVersion version, String accessToken) throws QTestInvalidFormatException {
        if (version == QTestVersion.V6) {
            return new V6Token(accessToken);
        } else if (version == QTestVersion.V7) {
            try {
                JsonObject js = new JsonObject().put("access_token", accessToken).put("token_type", "bearer");
                return new V7Token(js.toString());
            } catch (JsonException e) {
                throw QTestInvalidFormatException.createInvalidJsonFormatException(e.getMessage());
            }
        }
        return null;
    }
}
