package com.kms.katalon.execution.util;

import java.io.File;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

public class MailUtil {
    public enum MailSecurityProtocolType {
        None, SSL, TLS;
    }

    private static final String SUBJECT = "Katalon Report";

    public static void sendHtmlMail(EmailConfig conf) throws Exception {

        File reportFolder = conf.logFile.getParentFile();
        HtmlEmail email = new HtmlEmail();
        email.setHostName(conf.host);
        email.setSmtpPort(Integer.valueOf(conf.port));
        email.setFrom(conf.from, "");
        email.addTo(conf.tos);
        email.setSubject(SUBJECT + " for Test Suite: " + conf.suitePath);
        email.setAuthenticator(new DefaultAuthenticator(conf.username, conf.password));
        switch (conf.securityProtocol) {
        case SSL:
            email.setSSLOnConnect(true);
             email.setSslSmtpPort(conf.port);
            break;
        case TLS:
            email.setStartTLSEnabled(true);
            break;
        default:
            break;

        }

        StringBuilder sbHtml = buildMailContent(conf.signature);
        email.setHtmlMsg(sbHtml.toString());
        // set the alternative message
        email.setTextMsg("Dear Sir/Madam !\nHere is the summary of test suite run. Please see attached file For more details\nThis email is automatically sent. "
                + "Please do not reply\nThanks,\n" + conf.signature + "\n");

        // Attachment
        if (conf.sendAttachment) {
            attach(email, new File(reportFolder, reportFolder.getName() + ".html"));
        }
        email.send();
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

    public static void sendSummaryMail(EmailConfig conf, File csvFile, File logFile, List<Object[]> suitesSummaryForEmail)
            throws Exception {

        HtmlEmail email = new HtmlEmail();
        email.setHostName(conf.host);
        email.setFrom(conf.from, "");
        email.addTo(conf.tos);
        email.setSubject(SUBJECT + " for summary run");
        email.setAuthenticator(new DefaultAuthenticator(conf.username, conf.password));
        switch (conf.securityProtocol) {
        case SSL:
            email.setSSLOnConnect(true);
             email.setSslSmtpPort(conf.port);
            break;
        case TLS:
            email.setStartTLSEnabled(true);
            break;
        default:
            break;

        }
        //HTML content
        StringBuilder sbHtml = new StringBuilder();
        sbHtml.append("<html>");
        sbHtml.append("<head><title>Katalon Summary Report</title></head>");
        sbHtml.append("<body>");
        sbHtml.append("<div>Dear Sir, Madam !</div><br/>");
        sbHtml.append("<div>Here is the summary of test run:</div>");
        sbHtml.append("<br/>");
        for (Object[] arrSum : suitesSummaryForEmail) {
            String suiteName = (String) arrSum[0];
            Integer passed = (Integer) arrSum[1];
            Integer failed = (Integer) arrSum[2];
            Integer error = (Integer) arrSum[3];
            Integer notRun = (Integer) arrSum[4];
            String hostName = String.valueOf(arrSum[5]);
            String os = String.valueOf(arrSum[6]);
            String browser = String.valueOf(arrSum[7]);
            // Suite 1: 7 failed 13 passed
            sbHtml.append("Suite " + suiteName);
            sbHtml.append(" : ");
            sbHtml.append(passed > 0 ? passed + " passed " : "");
            sbHtml.append(failed > 0 ? failed + " failed " : "");
            sbHtml.append(error > 0 ? error + " error " : "");
            sbHtml.append(notRun > 0 ? notRun + " Not Run " : "");
            sbHtml.append(". On host " + hostName + ", OS " + os + ", browser " + browser);
            sbHtml.append("<br/>");
        }        
        sbHtml.append("<br/>");
        sbHtml.append("<div>For more details, please see attached file</div><br/>");
        sbHtml.append("<div>This email is automatically sent. Please do not reply<div><br/>");
        sbHtml.append("<div>Thanks,<div>");
        sbHtml.append("<div>" + conf.signature + "<div>");
        sbHtml.append("</body>");
        sbHtml.append("</html>");
        email.setHtmlMsg(sbHtml.toString());        
        // set the alternative message
        StringBuilder sbText = new StringBuilder();
        sbText.append("Dear Sir, Madam !\n\n");
        sbText.append("Here is the summary of test run:\n");
        for (Object[] arrSum : suitesSummaryForEmail) {
            String suiteName = (String) arrSum[0];
            Integer passed = (Integer) arrSum[1];
            Integer failed = (Integer) arrSum[2];
            Integer error = (Integer) arrSum[3];
            Integer notRun = (Integer) arrSum[4];
            String hostName = String.valueOf(arrSum[5]);
            String os = String.valueOf(arrSum[6]);
            String browser = String.valueOf(arrSum[7]);
            // Suite 1: 7 failed 13 passed
            sbText.append("Suite " + suiteName);
            sbText.append(" : ");
            sbText.append(passed > 0 ? passed + " passed " : "");
            sbText.append(failed > 0 ? failed + " failed " : "");
            sbText.append(error > 0 ? error + " error " : "");
            sbText.append(notRun > 0 ? notRun + " Not Run " : "");
            sbText.append(". On host " + hostName + ", OS " + os + ", browser " + browser);
            sbText.append("\n");
        }

        sbText.append("\nFor more details, please see attached file\n");
        sbText.append("\nThis email is automatically sent. Please do not reply\n\n");
        sbText.append("Thanks,\n");
        sbText.append(conf.signature);
        email.setTextMsg(sbText.toString());
        // Attachment
        if (conf.sendAttachment && csvFile != null && csvFile.exists()) {
            attachSummary(email, csvFile);
        }
        if (conf.sendAttachment && logFile != null && logFile.exists()) {
            attach(email, logFile);
        }
        email.send();
    }

    private static StringBuilder buildMailContent(String signature) {
        StringBuilder sb = new StringBuilder();
        // Header
        sb.append("<html>\n");
        sb.append("<head><title>Katalon Summary Report</title></head>\n");
        sb.append("<body>\n");
        sb.append("<div>Dear Sir/Madam !</div><br/>\n");
        sb.append("<div>Here is the summary of test suite run. Please see attached file For more details</div>\n");
        sb.append("<br/>\n");
        // Main content
        // sb.append(FileUtils.readFileToString(sumFile));
        // Footer
        // sb.append("<br/>\n");
        sb.append("<div>This email is automatically sent. Please do not reply<div><br/>\n");
        sb.append("<div>Thanks,<div>\n");
        sb.append("<div>" + signature + "<div>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb;
    }

    private static void attach(HtmlEmail email, File file) throws Exception {
        // Zip html report with its dependencies
        File tmpReportDir = new File(System.getProperty("java.io.tmpdir"), file.getParentFile().getName());
        if (tmpReportDir.exists()) {
            tmpReportDir.delete();
        }
        tmpReportDir.mkdir();
        for (File f : file.getParentFile().listFiles()) {
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

    public static class EmailConfig {
        public String host = "";
        public String port = "";
        public MailSecurityProtocolType securityProtocol = MailSecurityProtocolType.None;
        public String username;
        public String password;
        public String[] tos = {};
        public String from = "";
        public File logFile;
        public String suitePath = "";
        public String signature = "";
        public boolean sendAttachment = false;
    }

    private static File zip(String directory, String zipName) throws Exception {
        File folder = new File(directory);
        if (folder.isDirectory()) {
            File file = new File(folder.getParent() + File.separator + zipName + ".zip");
            if (file.exists())
                file.delete();
            ZipFile zipFile = new ZipFile(folder.getParent() + File.separator + zipName + ".zip");
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            zipFile.addFolder(directory, parameters);
            return new File(folder.getParent() + File.separator + zipName + ".zip");
        }
        return null;
    }
}