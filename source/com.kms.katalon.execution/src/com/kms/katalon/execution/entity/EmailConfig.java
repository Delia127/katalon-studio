package com.kms.katalon.execution.entity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;

public class EmailConfig {
    private String host;

    private String port;

    private MailSecurityProtocolType securityProtocol;

    private String username;

    private String password;

    private String[] tos;

    private String from;

    private String signature;

    private boolean sendAttachment;

    public EmailConfig() {
        setSendAttachment(false);
        setSecurityProtocol(MailSecurityProtocolType.None);
    }
    
    public void setSendAttachment(boolean sendAttachment) {
        this.sendAttachment = sendAttachment;
    }
    
    public boolean isSendAttachmentEnable() {
        return sendAttachment;
    }

    public boolean canSend() {
        return ArrayUtils.isNotEmpty(tos);
    }
    
    public void addRecipients(String recipients) {
        Set<String> allEmails = new LinkedHashSet<String>(Arrays.asList(tos));
        allEmails.addAll(Arrays.asList(MailUtil.getDistinctRecipients(recipients)));
        tos = allEmails.toArray(new String[allEmails.size()]);
    }

    public MailSecurityProtocolType getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(MailSecurityProtocolType securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getHost() {
        if (host == null) {
            host = "";
        }
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        if (port == null) {
            port = "";
        }
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getFrom() {
        if (from == null) {
            from = "";
        }
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSignature() {
        if (signature == null) {
            signature = "";
        }
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPassword() {
        if (password == null) {
            password = "";
        }
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        if (username == null) {
            username = "";
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[] getTos() {
        if (tos == null) {
            tos = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return tos;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }
}