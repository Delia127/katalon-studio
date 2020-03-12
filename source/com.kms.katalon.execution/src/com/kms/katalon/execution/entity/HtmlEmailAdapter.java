package com.kms.katalon.execution.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import com.kms.katalon.logging.LogUtil;

public class HtmlEmailAdapter implements IHtmlEmailAdapter {

    private static final int EMAIL_TIMEOUT = 600000;

    public static final String EMAIL_SEPARATOR = ";";

    public HtmlEmail adapt(EmailConfig conf, HtmlEmail vessel) {
        try {
            HtmlEmail email = vessel == null ? new HtmlEmail() : vessel;
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
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return null;
    }
}
