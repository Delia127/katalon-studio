package com.kms.katalon.integration.qtest.credential.impl;

import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.credential.IQTestToken;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

public class QTestCredentialImpl implements IQTestCredential {
    private QTestVersion version;
    private String serverUrl;
    private String username;
    private String password;
    private IQTestToken token;
    private boolean passwordEncryptionEnabled;

    @Override
    public QTestVersion getVersion() {
        return version;
    }

    public QTestCredentialImpl setVersion(QTestVersion version) {
        this.version = version;
        return this;
    }

    public String getServerUrl() {
        if (serverUrl == null) {
            serverUrl = "";
        }
        return serverUrl;
    }

    public QTestCredentialImpl setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public String getUsername() {
        if (username == null) {
            username = "";
        }
        return username;
    }

    public QTestCredentialImpl setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        if (password == null) {
            password = "";
        }
        return password;
    }

    public QTestCredentialImpl setPassword(String password) {
        this.password = password;
        return this;
    }

    public IQTestToken getToken() {
        return token;
    }

    public QTestCredentialImpl setToken(IQTestToken token) {
        this.token = token;
        return this;
    }

    @Override
    public boolean isEncryptionEnabled() {
        return passwordEncryptionEnabled;
    }

    public QTestCredentialImpl setPasswordEncryptionEnabled(boolean passwordEncryptionEnabled) {
        this.passwordEncryptionEnabled = passwordEncryptionEnabled;
        return this;
    }
}
