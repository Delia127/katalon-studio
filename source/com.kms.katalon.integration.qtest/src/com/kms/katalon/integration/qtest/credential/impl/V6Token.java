package com.kms.katalon.integration.qtest.credential.impl;

import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

public class V6Token extends AbstractQTestToken {
    
    public V6Token(String rawToken) {
       super(rawToken);
    }

    @Override
    public QTestVersion getVersion() {
        return QTestVersion.V6;
    }

    @Override
    public String getAccessTokenHeader() throws QTestInvalidFormatException {
        return rawToken;
    }

    @Override
    public String getAccessToken() throws QTestInvalidFormatException {
        return rawToken;
    }
}
