package com.kms.katalon.execution.util;

import static org.apache.commons.lang.StringUtils.split;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.setting.EmailSettingStore;
import com.kms.katalon.logging.LogUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class MailUtil {
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

    public static final String EMAIL_SEPARATOR = ";";

    private static final String SUBJECT = "Katalon Summary Report";

    private static final String EMAIL_HTML_TEMPLATE = "<html><head><style type=\"text/css\">body'{'margin:0;padding:0;min-width:100%;background-color:#f5f7fa;font-family:Tahoma,Droid Sans,Verdana,sans-serif;color:#60666d;font-size:14;font-style:normal;'}'table'{'border-collapse:collapse;border-spacing:0;margin:0 auto 24px;font-size:14;'}'td'{'padding:5px;word-break:break-word;word-wrap:break-word;vertical-align:middle;'}'.border td'{'border:1px solid #dddee1;'}'</style></head><body><center style=\"padding-bottom:24px\"><table width=\"600\" style=\"width:600px\"><tbody><tr height=\"101\" style=\"padding-top:24px;padding-bottom:24px\"><td width=\"50%\" style=\"width:50%;padding:0\"><img style=\"width: 150px;\" src=\"https://www.katalon.com/wp-content/themes/katalon/images/logo-katalon.png\" alt=\"KATALON LOGO\" /></td><td width=\"50%\" valign=\"middle\" style=\"width:50%;vertical-align:middle;padding:0\"><h2 style=\"margin:0;font-size:18px;color:#04a0dc;text-align:right\">Test Suite Execution Report</h2></td></tr><tr style=\"background-color:#fff\"><td style=\"border:1px solid #dddee1;padding:24px;word-break:break-word;word-wrap:break-word\" colspan=\"2\"><p>Dear Sir/Madam,<br><br>Your test suite has just finished its execution. Here is the summary report.</p><table class=\"border\" width=\"100%\" border=\"1\" bgcolor=\"#f5f7fa\" style=\"width:100%;background-color:#f5f7fa;border:1px solid #dddee1\"><tbody><tr><td width=\"24%\" style=\"width:24%\">Host Name</td><td colspan=\"3\" class=\"border\">{0}</td></tr><tr><td>Operating System</td><td colspan=\"3\">{1}</td></tr><tr><td class=\"border\">Browser</td><td colspan=\"3\">{2}</td></tr><tr><td>Test Suite</td><td colspan=\"3\">{3}</td></tr><tr><td>Result</td><td width=\"25%\" style=\"width:25%;color:green\">Passed: {4}</td><td width=\"25%\" style=\"width:25%;color:red\">Failed: {5}</td><td width=\"25%\" style=\"width:25%;color:red\">Error: {6}</td></tr></tbody></table><p>{7}<br><br>This email was sent automatically by Katalon System. Please do not reply.<br><br>Thanks,<br>{8}</p></td></tr></tbody></table></center></body></html>";

    private static final String EMAIL_TEXT_TEMPLATE = "Dear Sir/Madam,\n\nHere is the summary of test suite execution.\n\nHost Name:\t\t\t{0}\nOperating System:\t\t\t{1}\nBrowser:\t\t\t{2}\nTest Suite:\t\t\t{3}\nResult\t\t\tPassed: {4}\t\tFailed: {5}\t\tError: {6}\n\n{7}\n\nThis email was sent automatically by Katalon System. Please do not reply.\n\nThanks,\n{8}";

    private static final String EMAIL_TEST_TEMPLATE = "This is a test email from Katalon.";

    private static final String EMAIL_TEST_SUBJECT = "Katalon Test Email";

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
        ImageHtmlEmail email = initEmail(conf, EMAIL_TEST_SUBJECT);
        email.setMsg(EMAIL_TEST_TEMPLATE);
        email.send();
    }

    private static ImageHtmlEmail initEmail(EmailConfig conf, String subject) throws EmailException {
        ImageHtmlEmail email = new ImageHtmlEmail();
        email.setHostName(conf.getHost());
        email.setFrom(conf.getFrom(), "");
        email.addTo(conf.getTos());
        email.setSubject(subject);

        email.setAuthenticator(new DefaultAuthenticator(conf.getUsername(), conf.getPassword()));
        switch (conf.getSecurityProtocol()) {
            case SSL:
                email.setSSLOnConnect(true);
                email.setSslSmtpPort(conf.getPort());
                break;
            case TLS:
                email.setStartTLSEnabled(true);
                break;
            default:
                break;
        }
        return email;
    }

    public static void sendSummaryMail(EmailConfig conf, File csvFile, File logFolder,
            List<Object[]> suitesSummaryForEmail) throws Exception {
        if (conf == null || !conf.canSend()) {
            return;
        }

        ImageHtmlEmail email = initEmail(conf, SUBJECT);

        String emailMsg = "You can now go to your test project to view the execution report.";

        if (conf.isSendAttachmentEnable()) {
            // Attachment
            if (csvFile != null && csvFile.exists()) {
                attachSummary(email, csvFile);
            }
            if (logFolder != null && logFolder.exists()) {
                attach(email, logFolder);
            }
        }

        String suiteName = (String) suitesSummaryForEmail.get(0)[0];
        Integer passed = (Integer) suitesSummaryForEmail.get(0)[1];
        Integer failed = (Integer) suitesSummaryForEmail.get(0)[2];
        Integer error = (Integer) suitesSummaryForEmail.get(0)[3];
        // Integer incomplete = (Integer) suitesSummaryForEmail.get(0)[4];
        String hostName = ObjectUtils.toString(suitesSummaryForEmail.get(0)[5]);
        String os = ObjectUtils.toString(suitesSummaryForEmail.get(0)[6]);
        String browser = ObjectUtils.toString(suitesSummaryForEmail.get(0)[7]);

        // Prepare email message
        String htmlMessage = MessageFormat.format(EMAIL_HTML_TEMPLATE, hostName, os, browser, suiteName, passed, failed,
                error, emailMsg, conf.getSignature());
        String textMessage = MessageFormat.format(EMAIL_TEXT_TEMPLATE, hostName, os, browser, suiteName, passed, failed,
                error, emailMsg, conf.getSignature());

        // Define the base URL to resolve relative resource locations
        URL url = new URL("http://katalon.kms-technology.com");
        email.setDataSourceResolver(new DataSourceUrlResolver(url));

        // Set HTML formatted message
        email.setHtmlMsg(htmlMessage);

        // Set fallback text email message
        email.setTextMsg(textMessage);

        email.send();
    }

    private static void attach(HtmlEmail email, File folder) throws Exception {
        // Zip html report with its dependencies
        File tmpReportDir = new File(System.getProperty("java.io.tmpdir"), folder.getName());
        if (tmpReportDir.exists()) {
            tmpReportDir.delete();
        }
        tmpReportDir.mkdir();
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith(".html") || f.getName().endsWith(".csv")) {
                FileUtils.copyFileToDirectory(f, tmpReportDir);
            }
        }
        File zipFile = zip(tmpReportDir.getAbsolutePath(), tmpReportDir.getName());
        // Create the attachment
        EmailAttachment attachment = new EmailAttachment();
        attachment.setName(zipFile.getName());
        attachment.setURL(zipFile.toURI().toURL());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        // add the attachment
        email.attach(attachment);
    }

    private static void attachSummary(HtmlEmail email, File file) throws Exception {
        // Create the attachment
        EmailAttachment attachment = new EmailAttachment();
        attachment.setName(file.getName());
        attachment.setURL(file.toURI().toURL());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        // add the attachment
        email.attach(attachment);
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

    public static EmailConfig getDefaultEmailConfig() {
        EmailSettingStore store = new EmailSettingStore(ProjectController.getInstance().getCurrentProject());
        try {
            EmailConfig conf = new EmailConfig();
            conf.setHost(store.getHost());
            conf.setPort(store.getPort());
            conf.setFrom(store.getUsername());
            conf.setSecurityProtocol(MailSecurityProtocolType.valueOf(store.getProtocol()));
            conf.setUsername(store.getUsername());
            conf.setPassword(store.getPassword());
            conf.setSignature(store.getSignature());
            conf.setSendAttachment(store.isAddAttachment());
            conf.addRecipients(splitRecipientsString(store.getRecipients()));
            return conf;
        } catch (IOException e) {
            LogUtil.logError(e);
            return null;
        }
    }
}
