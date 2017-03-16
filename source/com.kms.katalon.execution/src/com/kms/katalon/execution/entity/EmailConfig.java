package com.kms.katalon.execution.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private boolean sendAttachment = false;

    public void setSendAttachment(boolean sendAttachment) {
        this.sendAttachment = sendAttachment;
    }

    public boolean isSendAttachmentEnable() {
        return sendAttachment;
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
}