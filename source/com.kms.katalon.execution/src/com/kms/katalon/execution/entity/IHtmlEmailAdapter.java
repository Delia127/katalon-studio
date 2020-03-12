package com.kms.katalon.execution.entity;

import org.apache.commons.mail.HtmlEmail;

public interface IHtmlEmailAdapter {
    /**
     * Adapt the given EmailConfig into the given HtmlEmail
     * 
     * @param conf EmailConfig
     * @param vessel HtmlEmail
     * @return the given HtmlEmail with populated fields
     */
    public HtmlEmail adapt(EmailConfig conf, HtmlEmail vessel);
}
