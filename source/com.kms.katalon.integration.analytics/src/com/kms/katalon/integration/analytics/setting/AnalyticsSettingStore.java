package com.kms.katalon.integration.analytics.setting;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
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

    public String getServerEndpoint(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_SERVER_ENDPOINT,
                AnalyticsStringConstants.ANALYTICS_SERVER_TARGET_ENDPOINT, encryptionEnabled);
    }

    public void setServerEndPoint(String serverEndpoint, boolean encryptionEnabled)
            throws IOException, GeneralSecurityException {
        setStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_SERVER_ENDPOINT, serverEndpoint, encryptionEnabled);
    }

    public String getEmail(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
//        return getStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_EMAIL, StringUtils.EMPTY,
//                encryptionEnabled);
    	return ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
    }

    public void setEmail(String email, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
//        setStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_EMAIL, email, encryptionEnabled);
    }

    public String getPassword(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
//        return getStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_PASSWORD, StringUtils.EMPTY,
//                encryptionEnabled);
    	String passwordDecode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
    	return CryptoUtil.decode(CryptoUtil.getDefault(passwordDecode));
    }

    public void setPassword(String rawPassword, boolean encryptionEnabled)
            throws IOException, GeneralSecurityException {
//        setStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_PASSWORD, rawPassword,
//                encryptionEnabled);
    }
    
	public String getApiKey(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
		return getStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_APIKEY, StringUtils.EMPTY,
				encryptionEnabled);
	}

	public void setApiKey(String rawPassword, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
		setStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_APIKEY, rawPassword,
				encryptionEnabled);
	}
  
	public void removeApiKey() throws IOException, GeneralSecurityException {
		setStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_APIKEY, "", true);
	}

    public boolean isEncryptionEnabled() throws IOException {
        return getBoolean(AnalyticsSettingStoreConstants.ANALYTICS_ENCRYPTION_ENABLED, false);
    }

    public void enableEncryption(boolean enabled) throws IOException {
        setProperty(AnalyticsSettingStoreConstants.ANALYTICS_ENCRYPTION_ENABLED, enabled);
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

    public String getToken(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_TOKEN, StringUtils.EMPTY,
                encryptionEnabled);
    }

    public void setToken(String token, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_TOKEN, token, encryptionEnabled);
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
