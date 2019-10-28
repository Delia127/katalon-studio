package com.kms.katalon.integration.analytics.setting;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    public String getServerEndpoint() {
        String server = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ON_PREMISE_SERVER);
        if (StringUtils.isEmpty(server)) {
            server = ApplicationInfo.getTestOpsServer();
        }
        return server;
    }

    public void setServerEndPoint(String serverEndpoint) {
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ON_PREMISE_SERVER, serverEndpoint, true);
    }

    public String getEmail() {
        String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ON_PREMISE_EMAIL);
        if (StringUtils.isEmpty(email)) {
            email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
        }
        return email;
    }

    public void setEmail(String email) {
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ON_PREMISE_EMAIL, email, true);
    }

    public String getPassword() throws IOException, GeneralSecurityException {
        String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ON_PREMISE_PASSWORD);
        if (StringUtils.isEmpty(encryptedPassword)) {
            encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
        }

        if (!StringUtils.isEmpty(encryptedPassword)) {
            return CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
        }
        return null;
    }

    public void setPassword(String rawPassword) throws UnsupportedEncodingException, GeneralSecurityException {
        String encryptedPassword = CryptoUtil.encode(CryptoUtil.getDefault(rawPassword));
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ON_PREMISE_PASSWORD, encryptedPassword, true);
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
    
    public void removeProperties() {
        List<String> properties = new ArrayList<>();
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_SERVER_ENDPOINT);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_EMAIL);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_PASSWORD);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_AUTO_SUBMIT);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_SCREENSHOT);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_LOG);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_TEST_RESULT_ATTACH_CAPTURED_VIDEOS);
        properties.add(AnalyticsSettingStoreConstants.ANALYTICS_ENCRYPTION_ENABLED);
        try {
            removeProperties(properties);
        } catch (IOException e) {
            //ignore
        }
    }
}
