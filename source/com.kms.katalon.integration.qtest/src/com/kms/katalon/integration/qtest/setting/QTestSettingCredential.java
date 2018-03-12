package com.kms.katalon.integration.qtest.setting;

import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.credential.IQTestToken;
import com.kms.katalon.integration.qtest.credential.QTestTokenManager;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;

public class QTestSettingCredential implements IQTestCredential {
    
    private String fProjectDir;
    private QTestVersion fVersion;

    private QTestSettingCredential(String projectDir) {
        setProjectDir(projectDir);
    }
    
    public static QTestSettingCredential getCredential(String projectDir) {
        return new QTestSettingCredential(projectDir);
    }

    @Override
    public String getServerUrl() {
        return QTestSettingStore.getServerUrl(QTestSettingStore.isEncryptionEnabled(fProjectDir), fProjectDir);
    }

    @Override
    public String getUsername() {
        return QTestSettingStore.getUsername(QTestSettingStore.isEncryptionEnabled(fProjectDir), fProjectDir);
    }

    @Override
    public String getPassword() {
        return QTestSettingStore.getPassword(isEncryptionEnabled(), fProjectDir);
    }

    @Override
    public IQTestToken getToken() {
        try {
            return QTestTokenManager.getToken(QTestSettingStore.getRawToken(isEncryptionEnabled(), fProjectDir));
        } catch (QTestInvalidFormatException e) {
            return null;
        }
    }

    public String getProjectDir() {
        return fProjectDir;
    }

    private void setProjectDir(String fProjectDir) {
        this.fProjectDir = fProjectDir;
    }

    @Override
    public QTestVersion getVersion() {
        if (fVersion == null) {
            fVersion = QTestSettingStore.getQTestVersion(fProjectDir);
        }
        return fVersion;
    }
    
    public void setVersion(QTestVersion version) {
        fVersion = version;
    }

    @Override
    public boolean isEncryptionEnabled() {
        return QTestSettingStore.isEncryptionEnabled(fProjectDir);
    }
}
