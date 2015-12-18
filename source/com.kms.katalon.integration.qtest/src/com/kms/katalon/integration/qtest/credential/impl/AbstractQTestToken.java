package com.kms.katalon.integration.qtest.credential.impl;

import com.kms.katalon.integration.qtest.credential.IQTestToken;

public abstract class AbstractQTestToken implements IQTestToken {
    
    protected String rawToken;
    
    public AbstractQTestToken(String rawToken) {
        setRawToken(rawToken);
    }

    private void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }

    @Override
    public String getRawToken() {
        return rawToken;
    }
    
}
