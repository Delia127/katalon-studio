package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.core.setting.ReportFormatType;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;

public class EmailConfig {

    private String host = "";

    private String port = "";

    private MailSecurityProtocolType securityProtocol = MailSecurityProtocolType.None;

    private String username = "";

    private String password = "";

    private Set<String> tos = new HashSet<String>();

    private String from = "";

    private String signature = "";

    private String subject = "";

    private String htmlTemplateForTestSuite = "";
    
    private String htmlTemplateForTestSuiteCollection = "";

    private String cc = "";

    private String bcc = "";

    private boolean sendAttachment = false;
    
    private boolean sendTestSuiteReportEnabled = true;
    
    private boolean sendReportTestFailedOnly = false;
    
    private boolean sendTestSuiteCollectionReportEnabled = false;
    
    private boolean skipInvidiualTestSuiteReport = false;
    
    private List<ReportFormatType> attachmentOptions;

    public void setSendAttachment(boolean sendAttachment) {
        this.sendAttachment = sendAttachment;
    }

    public boolean isSendAttachmentEnable() {
        return sendAttachment;
    }
    
    public boolean isSendTestSuiteReportEnabled() {
        return sendTestSuiteReportEnabled;
    }

    public void setSendTestSuiteReportEnabled(boolean enabled) {
        this.sendTestSuiteReportEnabled = enabled;
    }
    
    public void setSendReportTestFailedOnly(boolean enabled) {
        this.sendReportTestFailedOnly = enabled;
    }

    public boolean isSendReportTestFailedOnly() {
        return sendReportTestFailedOnly;
    }

    public boolean isSendTestSuiteCollectionReportEnabled() {
        return sendTestSuiteCollectionReportEnabled;
    }

    public void setSendTestSuiteCollectionReportEnabled(boolean enabled) {
        this.sendTestSuiteCollectionReportEnabled = enabled;
    }

    public boolean isSkipInvidiualTestSuiteReport() {
        return skipInvidiualTestSuiteReport;
    }

    public void setSkipInvidiualTestSuiteReport(boolean enabled) {
        this.skipInvidiualTestSuiteReport = enabled;
    }

    public boolean canSend() {
        return !tos.isEmpty();
    }

    public void addRecipients(Set<String> recipients) {
        tos.addAll(recipients);
    }

    public void addRecipients(List<String> recipients) {
        tos.addAll(recipients);
    }

    public void addRecipients(String recipients) {
        tos.addAll(MailUtil.splitRecipientsString(recipients));
    }

    public MailSecurityProtocolType getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(MailSecurityProtocolType securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[] getTos() {
        return tos.toArray(new String[tos.size()]);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlTemplateForTestSuite() {
        return htmlTemplateForTestSuite;
    }

    public void setHtmTemplateForTestSuite(String htmlTemplate) {
        this.htmlTemplateForTestSuite = htmlTemplate;
    }

    public String getHtmlTemplateForTestSuiteCollection() {
        return htmlTemplateForTestSuiteCollection;
    }

    public void setHtmlTemplateForTestSuiteCollection(String htmlTemplateForTestSuiteCollection) {
        this.htmlTemplateForTestSuiteCollection = htmlTemplateForTestSuiteCollection;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public List<ReportFormatType> getAttachmentOptions() {
        if (attachmentOptions == null) {
            attachmentOptions = new ArrayList<>();
        }
        return attachmentOptions;
    }

    public void setAttachmentOptions(List<ReportFormatType> attachmentOptions) {
        this.attachmentOptions = attachmentOptions;
    }
}
