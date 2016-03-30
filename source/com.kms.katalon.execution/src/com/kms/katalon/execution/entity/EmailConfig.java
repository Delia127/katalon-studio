package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;

public class EmailConfig implements ConsoleOptionContributor {
    public final static String SEND_EMAIL_OPTION = "sendMail";

    private String host = "";

    private String port = "";

    private MailSecurityProtocolType securityProtocol = MailSecurityProtocolType.None;

    private String username = "";

    private String password = "";

    private Set<String> tos = new HashSet<String>();

    private String from = "";

    private String signature = "";

    private boolean sendAttachment = false;

    private static ConsoleOption<String> sendEmailConsoleOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return SEND_EMAIL_OPTION;
        }
    };

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

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(sendEmailConsoleOption);
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (StringUtils.isBlank(argumentValue)) {
            return;
        }
        if (consoleOption == sendEmailConsoleOption) {
            addRecipients(argumentValue);
        }
    }
}