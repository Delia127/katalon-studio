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
        try {
            new JsonObject(rawToken);
            return new V7Token(rawToken);
        } catch (JsonException ex) {
            return new V6Token(rawToken);
        }
    }
    
    public static IQTestToken getToken(QTestVersion version, String rawToken) throws QTestInvalidFormatException {
        IQTestToken qTestToken = getToken(rawToken);
        
        if (qTestToken.getVersion().higherThan(version)) {
            throw new QTestInvalidFormatException("Token and version are not compatible");
        } else {
            return qTestToken;
        }
    }
}
