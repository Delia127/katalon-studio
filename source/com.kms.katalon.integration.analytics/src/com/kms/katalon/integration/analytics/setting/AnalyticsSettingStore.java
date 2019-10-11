package com.kms.katalon.integration.analytics.setting;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.analytics.constants.AnalyticsSettingStoreConstants;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.util.CryptoUtil;
import com.kms.katalon.logging.LogUtil;

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

    public String getServerEndpoint() throws IOException, GeneralSecurityException {
        return ApplicationInfo.getTestOpsServer();
    }

    public void setServerEndPoint(String serverEndpoint) throws IOException, GeneralSecurityException {
    }

    public String getEmail() throws IOException, GeneralSecurityException {
        return ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
    }

    public void setEmail(String email) throws IOException, GeneralSecurityException {
    }

    public String getPassword() throws IOException, GeneralSecurityException {
        String passwordDecode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
        return CryptoUtil.decode(CryptoUtil.getDefault(passwordDecode));
    }

    public void setPassword(String rawPassword) throws IOException, GeneralSecurityException {
    }

    public AnalyticsProject getProject() throws IOException {
        String projectJson = getString(AnalyticsSettingStoreConstants.ANALYTICS_PROJECT, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(projectJson) || !StringUtils.contains(projectJson, "null")) {
            try {
                AnalyticsProject project = new AnalyticsProject();
                project = JsonUtil.fromJson(projectJson, AnalyticsProject.class);
                return project;
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
        return null;
    }

    public void setProject(AnalyticsProject project) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_PROJECT, JsonUtil.toJson(project));
    }

    public AnalyticsTeam getTeam() throws IOException {
        String teamJson = getString(AnalyticsSettingStoreConstants.ANALYTICS_TEAM, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(teamJson)) {
            try {
                AnalyticsTeam team = new AnalyticsTeam();
                team = JsonUtil.fromJson(teamJson, AnalyticsTeam.class);
                return team;
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
        return null;
    }

    public void setTeam(AnalyticsTeam team) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_TEAM, JsonUtil.toJson(team));
    }

    public String getToken() throws IOException, GeneralSecurityException {
        return getStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_TOKEN, StringUtils.EMPTY,
                true);
    }

    public void setToken(String token) throws IOException, GeneralSecurityException {
        setStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_TOKEN, token, true);
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

    public AnalyticsOrganization getOrganization() {
        AnalyticsOrganization organization = new AnalyticsOrganization();
        String jsonObject = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ORGANIZATION);
        if (StringUtils.isNotBlank(jsonObject)) {
            try {
                organization = JsonUtil.fromJson(jsonObject, AnalyticsOrganization.class);
            } catch (IllegalArgumentException e) {
                LogUtil.logError(e);
            }
        }
        return organization;
    }
    
    public void removeProperty() {
        List<String> properties = new ArrayList<>();
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_SERVER_ENDPOINT);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_EMAIL);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_PASSWORD);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_AUTO_SUBMIT);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_SCREENSHOT);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_LOG);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_CAPTURED_VIDEOS);
        try {
            removeProperties(properties);
        } catch (IOException e) {
            //ignore
        }
    }
}
