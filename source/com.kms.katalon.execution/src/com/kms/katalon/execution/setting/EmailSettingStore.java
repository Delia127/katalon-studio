package com.kms.katalon.execution.setting;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class EmailSettingStore extends BundleSettingStore {

    private ScopedPreferenceStore mailPreferenceStore;

    public EmailSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(), FrameworkUtil.getBundle(EmailSettingStore.class).getSymbolicName(),
                false);
        mailPreferenceStore = getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
    }

    private String getStringFromSettingOrPrefs(String mailConfigSettingName) throws IOException {
        return getString(mailConfigSettingName,
                mailPreferenceStore.getString(mailConfigSettingName));
    }
    
    private boolean getBooleanFromSettingOrPrefs(String mailConfigSettingName) throws IOException {
        return getBoolean(mailConfigSettingName,
                mailPreferenceStore.getBoolean(mailConfigSettingName));
    }

    public String getHost() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_HOST);
    }

    public void setHost(String hostName) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_HOST, hostName);
    }

    public String getPort() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_PORT);
    }

    public void setPort(String port) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_PORT, port);
    }

    public boolean isAddAttachment() throws IOException {
        return getBooleanFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT);
    }

    public void setIsAddAttachment(boolean isAddAttachment) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT, isAddAttachment);
    }

    public String getUsername() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_USERNAME);
    }

    public void setUsername(String userName) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_USERNAME, userName);
    }

    public String getPassword() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_PASSWORD);
    }

    public void setPassword(String password) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_PASSWORD, password);
    }

    public String getProtocol() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_SECURITY_PROTOCOL);
    }

    public void setProtocol(String protocol) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SECURITY_PROTOCOL, protocol);
    }

    public String getRecipients() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS);
    }

    public void setRecipients(String recipients) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS, recipients);
    }

    public String getSignature() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_SIGNATURE);
    }

    public void setSignature(String signature) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SIGNATURE, signature);
    }
}
