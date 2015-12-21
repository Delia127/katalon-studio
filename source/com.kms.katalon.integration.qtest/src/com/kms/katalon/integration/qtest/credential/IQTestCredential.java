package com.kms.katalon.integration.qtest.credential;

import com.kms.katalon.integration.qtest.setting.QTestVersion;

public interface IQTestCredential {    
    public QTestVersion getVersion();

    public String getServerUrl();

    public String getUsername();

    public String getPassword();

    public IQTestToken getToken();
}
