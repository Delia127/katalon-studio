package com.kms.katalon.integration.jira.setting;

import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_AUTH_ENCRYPTION_ENABLED;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_AUTH_PASSWORD;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_AUTH_SERVER_URL;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_AUTH_USER;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_AUTH_USERNAME;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_INTEGRATION_ENABLED;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_SUBMIT_ATTACH_LOG;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_SUBMIT_ATTACH_SCREENSHOT;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_SUBMIT_JIRA_ISSUE_TYPE;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_SUBMIT_JIRA_PROJECT;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY;
import static com.kms.katalon.integration.jira.constant.StringConstants.PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.rest.client.api.domain.User;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.jira.JiraAPIURL;
import com.kms.katalon.integration.jira.JiraCredential;
import com.kms.katalon.integration.jira.constant.StringConstants;
import com.kms.katalon.integration.jira.entity.JiraIssueType;
import com.kms.katalon.integration.jira.entity.JiraProject;

public class JiraIntegrationSettingStore extends BundleSettingStore {

    public JiraIntegrationSettingStore(String projectDir) {
        super(projectDir, StringConstants.JIRA_BUNDLE_ID, false);
    }

    public boolean isIntegrationEnabled() throws IOException {
        //Disabled since v6.1.5 https://github.com/kms-technology/katalon/issues/2471
        return false;
    }

    public void enableIntegration(boolean enabled) throws IOException {
        setProperty(PREF_INTEGRATION_ENABLED, enabled);
    }

    public String getUsername(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(PREF_AUTH_USERNAME, StringUtils.EMPTY, encryptionEnabled);
    }

    public void saveUsername(String username, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(PREF_AUTH_USERNAME, username, encryptionEnabled);
    }

    public String getPassword(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(PREF_AUTH_PASSWORD, StringUtils.EMPTY, encryptionEnabled);
    }

    public void savePassword(String rawPassword, boolean encryptEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(PREF_AUTH_PASSWORD, rawPassword, encryptEnabled);
    }

    public boolean isEncryptionEnabled() throws IOException {
        return getBoolean(PREF_AUTH_ENCRYPTION_ENABLED, false);
    }

    public void enableEncryption(boolean enabled) throws IOException {
        setProperty(PREF_AUTH_ENCRYPTION_ENABLED, enabled);
    }

    public String getServerUrl(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        String decryptedServerUrl = getStringProperty(PREF_AUTH_SERVER_URL, StringUtils.EMPTY, encryptionEnabled);
        return JiraAPIURL.removeLastSplash(decryptedServerUrl);
    }

    public void saveServerUrl(String serverUrl, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(PREF_AUTH_SERVER_URL, serverUrl, encryptionEnabled);
    }

    public boolean isUseTestCaseNameAsSummaryEnabled() throws IOException {
        return getBoolean(PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY, true);
    }

    public void enableUseTestCaseNameAsSummary(boolean enabled) throws IOException {
        setProperty(PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY, enabled);
    }

    public boolean isAttachScreenshotEnabled() throws IOException {
        return getBoolean(PREF_SUBMIT_ATTACH_SCREENSHOT, true);
    }

    public void enableAttachScreenshot(boolean enabled) throws IOException {
        setProperty(PREF_SUBMIT_ATTACH_SCREENSHOT, enabled);
    }

    public boolean isSubmitTestResultAutomatically() throws IOException {
        return getBoolean(PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY, true);
    }

    public void enableSubmitTestResultAutomatically(boolean enabled) throws IOException {
        setProperty(PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY, enabled);
    }

    public boolean isAttachLogEnabled() throws IOException {
        return getBoolean(PREF_SUBMIT_ATTACH_LOG, true);
    }

    public void enableAttachLog(boolean enabled) throws IOException {
        setProperty(PREF_SUBMIT_ATTACH_LOG, enabled);
    }

    public StoredJiraObject<JiraProject> getStoredJiraProject() throws IOException {
        StoredJiraObject<JiraProject> instance = new StoredJiraObject<>(null, null);
        String objectAsString = getString(PREF_SUBMIT_JIRA_PROJECT, StringUtils.EMPTY);
        try {
            Type collectionType = new TypeToken<StoredJiraObject<JiraProject>>() {}.getType();
            StoredJiraObject<JiraProject> storedObject = JsonUtil.fromJson(objectAsString, collectionType);
            return storedObject != null ? storedObject : instance;
        } catch (IllegalArgumentException e) {
            return instance;
        }
    }

    public void saveStoredJiraProject(StoredJiraObject<JiraProject> storedJiraProject) throws IOException {
        setProperty(PREF_SUBMIT_JIRA_PROJECT, JsonUtil.toJson(storedJiraProject, false));
    }

    public StoredJiraObject<JiraIssueType> getStoredJiraIssueType() throws IOException {
        StoredJiraObject<JiraIssueType> instance = new StoredJiraObject<>(null, null);
        String objectAsString = getString(PREF_SUBMIT_JIRA_ISSUE_TYPE, StringUtils.EMPTY);
        try {
            Type collectionType = new TypeToken<StoredJiraObject<JiraIssueType>>() {}.getType();
            StoredJiraObject<JiraIssueType> storedObject = JsonUtil.fromJson(objectAsString, collectionType);
            return storedObject != null ? storedObject : instance;
        } catch (IllegalArgumentException e) {
            return instance;
        }
    }

    public void saveStoredJiraIssueType(StoredJiraObject<JiraIssueType> storedJiraIssueType) throws IOException {
        setProperty(PREF_SUBMIT_JIRA_ISSUE_TYPE, JsonUtil.toJson(storedJiraIssueType, false));
    }

    public User getJiraUser() throws IOException {
        return JsonUtil.fromJson(getString(PREF_AUTH_USER, StringUtils.EMPTY), User.class);
    }

    public void saveJiraUser(User user) throws IOException {
        setProperty(PREF_AUTH_USER, JsonUtil.toJson(user, false));
    }

    public JiraCredential getJiraCredential() throws IOException, GeneralSecurityException {
        JiraCredential credential = new JiraCredential();

        boolean authenticationEncrypted = isEncryptionEnabled();
        credential.setServerUrl(getServerUrl(authenticationEncrypted));
        credential.setUsername(getUsername(authenticationEncrypted));
        credential.setPassword(getPassword(authenticationEncrypted));
        return credential;
    }
}
