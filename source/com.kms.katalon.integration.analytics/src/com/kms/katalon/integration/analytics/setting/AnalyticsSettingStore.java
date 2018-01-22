package com.kms.katalon.integration.analytics.setting;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.analytics.constants.AnalyticsSettingStoreConstants;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.util.CryptoUtil;

public class AnalyticsSettingStore extends BundleSettingStore {

    public AnalyticsSettingStore(String projectDir) {
        super(projectDir, AnalyticsStringConstants.ANALYTICS_BUNDLE_ID, false);
    }

    public boolean isIntegrationEnabled() throws IOException {
        return getBoolean(AnalyticsSettingStoreConstants.ANALYTICS_INTEGRATION_ENABLE, false);
    }
    
    public void enableIntegration(boolean enabled) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_INTEGRATION_ENABLE, enabled);
    }
    
    public String getServerEndpoint() throws IOException {
        return getString(AnalyticsSettingStoreConstants.ANALYTICS_SERVER_ENDPOINT, 
                AnalyticsStringConstants.ANALYTICS_SERVER_TARGET_ENDPOINT);
    }
    
    public void setServerEndPoint(String serverEndpoint) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_SERVER_ENDPOINT, serverEndpoint);
    }
    
    public String getEmail() throws IOException {
        return getString(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_EMAIL, StringUtils.EMPTY);
    }
    
    public void setEmail(String email) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_EMAIL, email);
    }
    
    public String getPassword(boolean encrypted) throws IOException, GeneralSecurityException {
        String storedPassword = getString(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_PASSWORD, StringUtils.EMPTY);
        return encrypted ? CryptoUtil.decode(CryptoUtil.getDefault(storedPassword)) : storedPassword;
    }
    
    public void setPassword(String rawPassword, boolean encrypted) throws IOException, GeneralSecurityException {
        String storedPassword = encrypted ? CryptoUtil.encode(CryptoUtil.getDefault(rawPassword)) : rawPassword;
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_PASSWORD, storedPassword);
    }
    
    public boolean isPasswordEncryptionEnabled() throws IOException {
        return getBoolean(AnalyticsSettingStoreConstants.ANALYTICS_ENCRYPTION_ENABLE, false);
    }

    public void enablePasswordEncryption(boolean enabled) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_ENCRYPTION_ENABLE, enabled);
    }
    
    public AnalyticsProject getProject() throws IOException {
        AnalyticsProject project = new AnalyticsProject();
        String projectJson = getString(AnalyticsSettingStoreConstants.ANALYTICS_PROJECT, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(projectJson)) {
            try {
                project = JsonUtil.fromJson(projectJson, AnalyticsProject.class);
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
        return project;
    }
    
    public void setProject(AnalyticsProject project) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_PROJECT, JsonUtil.toJson(project));
    }
    
    public AnalyticsTeam getTeam() throws IOException {
        AnalyticsTeam team = new AnalyticsTeam();
        String teamJson = getString(AnalyticsSettingStoreConstants.ANALYTICS_TEAM, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(teamJson)) {
            try {
                team = JsonUtil.fromJson(teamJson, AnalyticsTeam.class);
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
        return team;
    }
    
    public void setTeam(AnalyticsTeam team) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_TEAM, JsonUtil.toJson(team));
    }
    
    public String getToken() throws IOException {
        return getString(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_TOKEN, StringUtils.EMPTY);
    }
    
    public void setToken(String token) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_TOKEN, token);
    }
    
    public boolean isAutoSubmit() throws IOException {
        return getBoolean(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_AUTO_SUBMIT, false);
    }
    
    public void setAutoSubmit(boolean autoSubmit) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_AUTO_SUBMIT, autoSubmit);
    }
    
    public boolean isAttachScreenshot() throws IOException {
        return getBoolean(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_SCREENSHOT, false);
    }
    
    public void setAttachScreenshot(boolean attachScreenshot) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_SCREENSHOT, attachScreenshot);
    }
    
    public boolean isAttachLog() throws IOException {
        return getBoolean(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_LOG, false);
    }
    
    public void setAttachLog(boolean attachLog) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_LOG, attachLog);
    }
    
    public boolean isAttachCapturedVideos() throws IOException {
        return getBoolean(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_CAPTURED_VIDEOS, false);
    }
    
    public void setAttachCapturedVideos(boolean capturedVideos) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_CAPTURED_VIDEOS, capturedVideos);
    }
}
