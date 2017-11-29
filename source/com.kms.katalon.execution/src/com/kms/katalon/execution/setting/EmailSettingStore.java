package com.kms.katalon.execution.setting;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.setting.ReportFormatType;
import com.kms.katalon.core.util.internal.JarUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class EmailSettingStore extends BundleSettingStore {
    private static final String KATALON_STUDIO_EMAIL_SIGNATURE = "Katalon Studio";

    private static final String RESOURCES_TEMPLATE_EMAIL_FOLDER = "resources/template/email";

    private static final String EMAIL_TEMPLATE_HTML = "default_template.html";

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

    public String getEmailHTMLTemplate() throws IOException, URISyntaxException {
        return getString(ExecutionPreferenceConstants.MAIL_CONFIG_HTML_TEMPLATE, getDefaultEmailHTMLTemplate());
    }

    public String getDefaultEmailHTMLTemplate() throws IOException, URISyntaxException {
        String emailHtmlTemplate = FileUtils.readFileToString(new File(getTemplateFolder(), EMAIL_TEMPLATE_HTML));
        return emailHtmlTemplate.replace(KATALON_STUDIO_EMAIL_SIGNATURE,
                StringUtils.defaultIfEmpty(getSignature(), KATALON_STUDIO_EMAIL_SIGNATURE));
    }

    public File getTemplateFolder() throws IOException, URISyntaxException {
        File emailTempFolderRoot = ClassPathResolver.getConfigurationFolder();
        File emailTemplateFolder = new File(emailTempFolderRoot, RESOURCES_TEMPLATE_EMAIL_FOLDER);
        if (!emailTemplateFolder.exists()) {
            JarUtil.getFiles(this.getClass(), RESOURCES_TEMPLATE_EMAIL_FOLDER, emailTemplateFolder);
        }
        return emailTemplateFolder;
    }

    public void setHTMLTemplate(String htmlTemplate) throws IOException {
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_HTML_TEMPLATE, htmlTemplate);
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
        setProperty(ExecutionPreferenceConstants.MAIL_CONFIG_CC, StringUtils.defaultString(bcc));
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
}
