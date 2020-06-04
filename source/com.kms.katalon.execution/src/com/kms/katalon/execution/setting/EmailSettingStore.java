package com.kms.katalon.execution.setting;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.setting.ReportFormatType;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.util.EmailTemplateUtil;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class EmailSettingStore extends BundleSettingStore {
    private ScopedPreferenceStore mailPreferenceStore;

    public EmailSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(), FrameworkUtil.getBundle(EmailSettingStore.class).getSymbolicName(),
                false);
        mailPreferenceStore = getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
    }

    private String getStringFromSettingOrPrefs(String mailConfigSettingName) throws IOException {
        return getString(mailConfigSettingName, mailPreferenceStore.getString(mailConfigSettingName));
    }

    private boolean getBooleanFromSettingOrPrefs(String mailConfigSettingName) throws IOException {
        return getBoolean(mailConfigSettingName, mailPreferenceStore.getBoolean(mailConfigSettingName));
    }

    public boolean isEncryptionEnabled() throws IOException {
        return getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_ENABLE_ENCRYPTION, false);
    }

    public void enableAuthenticationEncryption(boolean enabled) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_ENABLE_ENCRYPTION, enabled);
    }

    public String getHost(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_HOST, StringUtils.EMPTY, encryptionEnabled);
    }

    public void setHost(String hostName, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_HOST, hostName, encryptionEnabled);
    }

    public String getPort(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_PORT, StringUtils.EMPTY, encryptionEnabled);
    }

    public void setPort(String port, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_PORT, port, encryptionEnabled);
    }

    public boolean isAddAttachment() throws IOException {
        return getBooleanFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT);
    }

    public void setIsAddAttachment(boolean isAddAttachment) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT, isAddAttachment);
    }

    public String getUsername(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_USERNAME, StringUtils.EMPTY,
                encryptionEnabled);
    }

    public void setUsername(String username, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_USERNAME, username, encryptionEnabled);
    }

    public String getPassword(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_PASSWORD, StringUtils.EMPTY,
                encryptionEnabled);
    }

    public void setPassword(String password, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_PASSWORD, password, encryptionEnabled);
    }

    public String getProtocol(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SECURITY_PROTOCOL, MailSecurityProtocolType.None.toString(),
                encryptionEnabled);
    }

    public void setProtocol(String protocol, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SECURITY_PROTOCOL, protocol, encryptionEnabled);
    }

    public boolean useUsernameAsSender() throws IOException {
        return getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_USE_USERNAME_AS_SENDER, true);
    }
    
    public void setUseUsernameAsSender(boolean useUsernameAsSender) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_USE_USERNAME_AS_SENDER, useUsernameAsSender);
    }
    
    public String getSender() throws IOException {
        return getString(ExecutionPreferenceConstants.MAIL_CONFIG_SENDER, StringUtils.EMPTY);
    }
    
    public void setSender(String sender) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SENDER, sender);
    }
    
    public String getRecipients(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS, StringUtils.EMPTY,
                encryptionEnabled);
    }

    public void setRecipients(String recipients, boolean encryptionEnabled)
            throws IOException, GeneralSecurityException {
        setStringProperty(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS, recipients, encryptionEnabled);
    }

    public String getSignature() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_SIGNATURE);
    }

    public void setSignature(String signature) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SIGNATURE, signature);
    }
    

    public String getEmailHTMLTemplateForTestSuite() throws IOException, URISyntaxException {
        return getString(ExecutionPreferenceConstants.MAIL_CONFIG_HTML_TEMPLATE, getDefaultEmailHTMLTemplateForTestSuite());
    }

    private String getDefaultEmailHTMLTemplateForTestSuite() throws IOException, URISyntaxException {
        return EmailTemplateUtil.getHTMLTemplateForTestSuite(getSignature());
    }
    
    public String getEmailHTMLTemplateForTestSuiteCollection() throws IOException, URISyntaxException {
        return getString(ExecutionPreferenceConstants.MAIL_CONFIG_COLLECTION_HTML_TEMPLATE, getDefaultEmailHTMLTemplateForTestSuiteCollection());
    }

    private String getDefaultEmailHTMLTemplateForTestSuiteCollection() throws IOException, URISyntaxException {
        return EmailTemplateUtil.getEmailHTMLTemplateForTestSuiteCollection();
    }

    public void setHTMLTemplateForTestSuite(String htmlTemplate) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_HTML_TEMPLATE, htmlTemplate);
    }
    
    public void setHTMLTemplateForTestSuiteCollection(String htmlTemplate) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_COLLECTION_HTML_TEMPLATE, htmlTemplate);
    }

    public String getEmailSubject() throws IOException {
        return getString(ExecutionPreferenceConstants.MAIL_CONFIG_SUBJECT,
                ExecutionMessageConstants.PREF_DEFAULT_EMAIL_SUBJECT);
    }

    public void setEmailSubject(String subject) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SUBJECT, subject);
    }

    public String getEmailCc() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_CC);
    }

    public void setEmailCc(String cc) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_CC, StringUtils.defaultString(cc));
    }

    public String getEmailBcc() throws IOException {
        return getStringFromSettingOrPrefs(ExecutionPreferenceConstants.MAIL_CONFIG_BCC);
    }

    public void setEmailBcc(String bcc) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_BCC, StringUtils.defaultString(bcc));
    }

    public List<ReportFormatType> getReportFormatOptions() throws IOException {
        String reportFormatOptAsJson = getString(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_FORMAT,
                StringUtils.EMPTY);
        if (StringUtils.isEmpty(reportFormatOptAsJson)) {
            return Arrays.asList(ReportFormatType.HTML, ReportFormatType.CSV);
        }
        return Arrays.asList(JsonUtil.fromJson(reportFormatOptAsJson, ReportFormatType[].class));
    }

    public void setReportFormatOptions(List<ReportFormatType> reportFormatOptions) throws IOException {
        String reportFormatOptAsJson = JsonUtil.toJson(reportFormatOptions.toArray(new ReportFormatType[0]));
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_FORMAT, reportFormatOptAsJson);
    }

    public boolean isSendEmailTestFailedOnly() throws IOException {
        return getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_SEND_REPORT_TEST_FAILED_ONLY, false);
    }

    public void setSendEmailTestFailedOnly(boolean enabled) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SEND_REPORT_TEST_FAILED_ONLY, enabled);
    }
    
    public boolean isSendTestSuiteReportEnabled() throws IOException {
        return getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_SEND_TEST_SUITE_REPORT, true);
    }
    
    public void setSendTestSuiteReportEnabled(boolean enabled) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SEND_TEST_SUITE_REPORT, enabled);
    }
    
    public boolean isSendTestSuiteCollectionReportEnabled() throws IOException {
        return getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_SEND_TEST_SUITE_COLLECTION_REPORT, false);
    }
    
    public void setSendTestSuiteCollectionReportEnabled(boolean enabled) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SEND_TEST_SUITE_COLLECTION_REPORT, enabled);
    }
    
    public boolean isSkipInvidualTestSuiteReport() throws IOException {
        return getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_SKIP_INDIVIDUAL_TEST_SUITE_REPORT, false);
    }
    
    public void setSkipIndividualTestSuiteReport(boolean enabled) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_SKIP_INDIVIDUAL_TEST_SUITE_REPORT, enabled);
    }
}
