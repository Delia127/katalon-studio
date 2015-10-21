package com.kms.katalon.integration.qtest.setting;

import com.kms.katalon.integration.qtest.credential.IQTestCredential;

public class QTestSettingCredential implements IQTestCredential {

    private String fProjectDir;

    public QTestSettingCredential(String projectDir) {
        setProjectDir(projectDir);
    }

    @Override
    public String getServerUrl() {
        return QTestSettingStore.getServerUrl(getProjectDir());
    }

    @Override
    public String getUsername() {
        return QTestSettingStore.getUsername(getProjectDir());
    }

    @Override
    public String getPassword() {
        return QTestSettingStore.getPassword(getProjectDir());
    }

    @Override
    public String getToken() {
        return QTestSettingStore.getToken(getProjectDir());
    }

    public String getProjectDir() {
        return fProjectDir;
    }

    private void setProjectDir(String fProjectDir) {
        this.fProjectDir = fProjectDir;
    }

}
