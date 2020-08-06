package com.kms.katalon.execution.util;

import static org.apache.commons.lang.StringUtils.split;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.FileDataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.setting.ReportFormatType;
import com.kms.katalon.core.util.StrSubstitutor;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportItemDescription;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.setting.EmailSettingStore;
import com.kms.katalon.execution.setting.EmailVariableBinding;
import com.kms.katalon.execution.webservice.VariableEvaluator;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.logging.LogUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class MailUtil {
    private static final long EMAIL_WARNING_SIZE = 10 * 1024 * 1024L;

    private static final int EMAIL_TIMEOUT = 600000;

    public static final String EMAIL_SEPARATOR = ";";

    public enum MailSecurityProtocolType {
        None, SSL, TLS;

        public static String[] getStringValues() {
            MailSecurityProtocolType[] allSecurityProtocolTypes = values();
            String[] stringValues = new String[allSecurityProtocolTypes.length];
            for (int index = 0; index < allSecurityProtocolTypes.length; index++) {
                stringValues[index] = allSecurityProtocolTypes[index].toString();
            }
            return stringValues;
        }
    }

    public static String[][] getMailSecurityProtocolTypeArrayValues() {
        MailSecurityProtocolType[] allSecurityProtocolTypes = MailSecurityProtocolType.values();
        String[][] arrayValues = new String[allSecurityProtocolTypes.length][2];
        for (int i = 0; i < allSecurityProtocolTypes.length; i++) {
            arrayValues[i][0] = allSecurityProtocolTypes[i].toString();
            arrayValues[i][1] = allSecurityProtocolTypes[i].toString();
        }
        return arrayValues;
    }

    public static void sendTestMail(EmailConfig conf) throws Exception {
        HtmlEmail email = initEmail(conf);
        email.setHtmlMsg("<html>"
                + GroovyStringUtil.evaluate(conf.getHtmlTemplateForTestSuite(), EmailVariableBinding.getTestEmailVariables())
                + "</html>");
        email.send();
    }

    private static HtmlEmail initEmail(EmailConfig conf) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("utf-8");
        email.setHostName(conf.getHost());
        email.setFrom(conf.getFrom(), "");
        email.setSubject(conf.getSubject());

        String cc = conf.getCc();
        if (StringUtils.isNotEmpty(cc)) {
            email.addCc(StringUtils.split(cc, EMAIL_SEPARATOR));
        }
        String bcc = conf.getBcc();
        if (StringUtils.isNotEmpty(bcc)) {
            email.addBcc(StringUtils.split(bcc, EMAIL_SEPARATOR));
        }
        email.addTo(conf.getTos());
        email.setSubject(conf.getSubject());
        email.setSocketConnectionTimeout(EMAIL_TIMEOUT);
        email.setSocketTimeout(EMAIL_TIMEOUT);

        email.setAuthenticator(new DefaultAuthenticator(conf.getUsername(), conf.getPassword()));
        switch (conf.getSecurityProtocol()) {
            case None:
                email.setSmtpPort(Integer.parseInt(conf.getPort()));
                break;
            case SSL:
                email.setSSLOnConnect(true);
                email.setSslSmtpPort(conf.getPort());
                break;
            case TLS:
                email.setStartTLSEnabled(true);
                email.setSmtpPort(Integer.parseInt(conf.getPort()));
                break;
            default:
                break;
        }
        return email;
    }

    public static void sendSummaryMailForTestSuite(EmailConfig conf, TestSuiteLogRecord suiteLogRecord,
            Map<String, Object> variables) throws Exception {
        if (conf == null || !conf.canSend()) {
            return;
        }

        HtmlEmail email = initEmail(conf);
        EmailAttachment attachment = null;
        File attachedFile = null;
        if (conf.isSendAttachmentEnable()) {
            attachment = attach(conf.getAttachmentOptions(), suiteLogRecord);
            if (attachment != null) {
                attachedFile = new File(attachment.getURL().toURI());
                email.attach(
                        new FileDataSource(attachedFile), 
                        attachment.getName(),
                        attachment.getDescription(),
                        attachment.getDisposition());
            }
        }

        // Set HTML formatted message
        email.setHtmlMsg("<html>" + GroovyStringUtil.evaluate(conf.getHtmlTemplateForTestSuite(), variables)
                + "</html>");

        try {
            email.send();
        } finally {
            if (attachment != null) {
                try {
                    FileUtils.forceDelete(attachedFile);
                } catch (IOException e) {
                    LogUtil.logError(e);
                }
            }
        }
    }

    private static EmailAttachment attach(List<ReportFormatType> attachmentOptions, TestSuiteLogRecord suiteLogRecord)
            throws Exception {
        File logFolder = new File(suiteLogRecord.getLogFolder());
        
        // Zip html report with its dependencies
        File tmpReportDir = new File(System.getProperty("java.io.tmpdir"),
                logFolder.getName() + "_" + System.currentTimeMillis());
        if (tmpReportDir.exists()) {
            tmpReportDir.delete();
        }
        tmpReportDir.mkdir();
        for (File f : logFolder.listFiles()) {
            String fileName = f.getName();
            if ((fileName.endsWith(".html") && attachmentOptions.contains(ReportFormatType.HTML))
                    || (fileName.endsWith(".csv") && attachmentOptions.contains(ReportFormatType.CSV))
                    || (isLogFile(fileName) && attachmentOptions.contains(ReportFormatType.LOG))
                    || (fileName.endsWith(".pdf") && attachmentOptions.contains(ReportFormatType.PDF))) {
                FileUtils.copyFileToDirectory(f, tmpReportDir);
            }
        }
        if (tmpReportDir.listFiles() != null && tmpReportDir.listFiles().length > 0) {
            File zipFile = zip(tmpReportDir.getAbsolutePath(), tmpReportDir.getName());
    
            if (zipFile.length() > EMAIL_WARNING_SIZE) {
                LogUtil.printOutputLine(ExecutionMessageConstants.MSG_EMAIL_ATTACHMENT_EXCEEDS_SIZE);
            }
            // Create the attachment
            EmailAttachment attachment = new EmailAttachment();
            attachment.setName(zipFile.getName());
            attachment.setURL(zipFile.toURI().toURL());
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
    
            return attachment;
        } else {
            return null;
        }
    }
    
    public static void sendSummaryMailForTestSuiteCollection(EmailConfig conf, ReportCollectionEntity reportEntity,
            Map<String, Object> variables) throws Exception {
        if (conf == null || !conf.canSend()) {
            return;
        }

        HtmlEmail email = initEmail(conf);

        EmailAttachment attachment = addAttachment(email, conf, reportEntity);

        email.setHtmlMsg("<html>" + GroovyStringUtil.evaluate(conf.getHtmlTemplateForTestSuiteCollection(), variables)
                + "</html>");

        try {
            email.send();
        } finally {
            if (attachment != null) {
                try {
                    FileUtils.forceDelete(new File(attachment.getURL().toURI()));
                } catch (IOException e) {
                    LogUtil.logError(e);
                }
            }
        }
    }

    private static EmailAttachment addAttachment(HtmlEmail email, EmailConfig conf,
            ReportCollectionEntity reportEntity) throws Exception {
        EmailAttachment attachment = null;
        if (conf.isSendAttachmentEnable()) {
            File tmpReportDir = prepareAttachmentFiles(reportEntity, conf.getAttachmentOptions());

            if (tmpReportDir != null && tmpReportDir.listFiles() != null && tmpReportDir.listFiles().length > 0) {
                File zipFile = zip(tmpReportDir.getAbsolutePath(), tmpReportDir.getName());
                if (zipFile.length() > EMAIL_WARNING_SIZE) {
                    LogUtil.printOutputLine(ExecutionMessageConstants.MSG_EMAIL_ATTACHMENT_EXCEEDS_SIZE);
                }

                attachment = new EmailAttachment();
                attachment.setName(zipFile.getName());
                attachment.setURL(zipFile.toURI().toURL());
                attachment.setDisposition(EmailAttachment.ATTACHMENT);

                try {
                    FileUtils.forceDelete(tmpReportDir);
                } catch (IOException e) {
                    LogUtil.logError(e);
                }
            }

            if (attachment != null) {
                File attachedFile = new File(attachment.getURL().toURI());
                email.attach(new FileDataSource(attachedFile), attachment.getName(), attachment.getDescription(),
                        attachment.getDisposition());
            }
        }
        return attachment;
    }

    private static File prepareAttachmentFiles(ReportCollectionEntity reportEntity,
            List<ReportFormatType> attachmentOptions) throws IOException {
        File tmpReportDir = Files.createTempDirectory("").toFile();

        String projectFolder = reportEntity.getProject().getFolderLocation();
        List<ReportItemDescription> reportItemDescriptions = reportEntity.getReportItemDescriptions();
        Set<String> copiedPaths = new HashSet<>();
        for (ReportItemDescription desc : reportItemDescriptions) {
            File copied = (new File(projectFolder, desc.getReportLocation()))
                .getParentFile()
                .getParentFile();
            
            if (!copiedPaths.contains(copied.getAbsolutePath())) {
                FileUtils.copyDirectoryToDirectory(copied, tmpReportDir);
                copiedPaths.add(copied.getAbsolutePath());
            }
        }

        Files.walk(Paths.get(tmpReportDir.toURI()))
            .filter(Files::isRegularFile)
            .filter(p -> !attachmentIncludes(attachmentOptions, p.toFile()))
            .map(Path::toFile)
            .forEach(FileUtils::deleteQuietly);

        return tmpReportDir;
    }

    private static boolean attachmentIncludes(List<ReportFormatType> attachmentOptions, File file) {
        String fileName = file.getName();
        return (fileName.endsWith(".html") && attachmentOptions.contains(ReportFormatType.HTML))
                || (fileName.endsWith(".csv") && attachmentOptions.contains(ReportFormatType.CSV))
                || (isLogFile(fileName) && attachmentOptions.contains(ReportFormatType.LOG))
                || (fileName.endsWith(".pdf") && attachmentOptions.contains(ReportFormatType.PDF)
                || (ReportUtil.JUNIT_REPORT_FILE_NAME.equals(fileName)));
    }
    
    private static boolean isLogFile(String fileName) {
        return fileName.endsWith(".log") || fileName.endsWith(".meta") || fileName.endsWith("execution.properties");
    }

    private static File zip(String directory, String zipName) throws Exception {
        File folder = new File(directory);
        if (folder.isDirectory()) {
            File file = new File(folder.getParent() + File.separator + zipName + ".zip");
            if (file.exists()) {
                file.delete();
            }
            ZipFile zipFile = new ZipFile(folder.getParent() + File.separator + zipName + ".zip");
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            zipFile.addFolder(directory, parameters);
            return new File(folder.getParent() + File.separator + zipName + ".zip");
        }
        return null;
    }

    public static List<String> splitRecipientsString(String recipients) {
        if (StringUtils.isBlank(recipients)) {
            return Collections.emptyList();
        }
        return Arrays.asList(split(StringUtils.deleteWhitespace(recipients), EMAIL_SEPARATOR));
    }

    public static Set<String> getDistinctRecipients(String... recipientsList) {
        if (recipientsList == null) {
            return Collections.emptySet();
        }
        Set<String> recipientCollector = new LinkedHashSet<String>();
        for (String recipients : recipientsList) {
            recipientCollector.addAll(splitRecipientsString(recipients));
        }
        return recipientCollector;
    }

    public static EmailConfig getDefaultEmailConfig(ProjectEntity project) {
        if (project == null) {
            return null;
        }
        EmailSettingStore store = new EmailSettingStore(project);
        try {
            boolean encryptionEnabled = store.isEncryptionEnabled();
            EmailConfig conf = new EmailConfig();
            conf.setHost(store.getHost(encryptionEnabled));
            conf.setPort(store.getPort(encryptionEnabled));

            String sender = store.getSender();
            if (store.useUsernameAsSender()) {
                sender = store.getUsername(encryptionEnabled);
            }
            conf.setFrom(sender);

            conf.setSecurityProtocol(MailSecurityProtocolType.valueOf(store.getProtocol(encryptionEnabled)));
            conf.setUsername(store.getUsername(encryptionEnabled));
            conf.setPassword(store.getPassword(encryptionEnabled));
            conf.setSignature(store.getSignature());
            conf.setSendAttachment(store.isAddAttachment());
            conf.setCc(store.getEmailCc());
            conf.setBcc(store.getEmailBcc());
            conf.addRecipients(splitRecipientsString(store.getRecipients(encryptionEnabled)));
            conf.setSubject(store.getEmailSubject());
            conf.setHtmTemplateForTestSuite(store.getEmailHTMLTemplateForTestSuite());
            conf.setAttachmentOptions(store.getReportFormatOptions());
            conf.setSendTestSuiteReportEnabled(store.isSendTestSuiteReportEnabled());
            conf.setSendReportTestFailedOnly(store.isSendEmailTestFailedOnly());
            conf.setHtmlTemplateForTestSuiteCollection(store.getEmailHTMLTemplateForTestSuiteCollection());
            conf.setSendTestSuiteCollectionReportEnabled(store.isSendTestSuiteCollectionReportEnabled());
            conf.setSkipInvidiualTestSuiteReport(store.isSkipInvidualTestSuiteReport());

            return conf;
        } catch (IOException | GeneralSecurityException | URISyntaxException e) {
            LogUtil.logError(e);
            return null;
        }
    }

    public static EmailConfig overrideEmailSettings(EmailConfig emailConfig, ExecutionProfileEntity executionProfile,
            Map<String, Object> overridingGlobalVariables) {
        VariableEvaluator evaluator = new VariableEvaluator();
        Map<String, Object> evaluatedVariables = new HashMap<>();
        try {
            evaluatedVariables = evaluator.evaluate(new HashMap<String, String>(), executionProfile,
                    overridingGlobalVariables);
        } catch (Exception e) {
            LogUtil.printAndLogError(e);
        }
        StrSubstitutor substitutor = new StrSubstitutor(
                Collections.<String, Object>unmodifiableMap(evaluatedVariables));
        emailConfig.setFrom(substitutor.replace(emailConfig.getFrom()));
        Set<String> tos = new HashSet<>();
        for (String recipient : emailConfig.getTos()) {
            tos.add(substitutor.replace(recipient));
        }
        emailConfig.setTos(tos);
        emailConfig.setCc(substitutor.replace(emailConfig.getCc()));
        emailConfig.setBcc(substitutor.replace(emailConfig.getBcc()));
        emailConfig.setSubject(substitutor.replace(emailConfig.getSubject()));
        return emailConfig;
    }
}
