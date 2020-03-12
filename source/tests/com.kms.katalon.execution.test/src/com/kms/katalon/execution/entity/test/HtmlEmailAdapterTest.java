package com.kms.katalon.execution.entity.test;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.entity.HtmlEmailAdapter;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;

public class HtmlEmailAdapterTest {
    EmailConfig conf;

    HtmlEmailAdapter adapter;

    @Before
    public void prepare() throws EmailException {
        conf = new EmailConfig();
        conf.setHost("custom_host");
        conf.setFrom("from@katalon.com");
        conf.setSubject("A song");
        conf.setCc("cc1@katalon.com;cc2@katalon.com;cc3@katalon.com");
        conf.setBcc("bcc1@katalon.com;bcc2@katalon.com;bcc3@katalon.com");
        conf.addRecipients("test1@katalon.com");
        conf.addRecipients("test2@katalon.com");
        conf.setUsername("katalon");

        adapter = new HtmlEmailAdapter();
    }

    @Test
    public void testBasicInformationAreGivenToHtmlEmailCorrectly() throws EmailException {
        HtmlEmail mockedEmail = Mockito.mock(HtmlEmail.class);

        adapter.adapt(conf, mockedEmail);

        HtmlEmail email = Mockito.verify(mockedEmail);
        email.setCharset("utf-8");
        email.setHostName(conf.getHost());
        email.setFrom(conf.getFrom(), "");
        email.setSubject(conf.getSubject());
        String cc = conf.getCc();
        if (StringUtils.isNotEmpty(cc)) {
            email.addCc(StringUtils.split(cc, ","));
        }
        String bcc = conf.getBcc();
        if (StringUtils.isNotEmpty(bcc)) {
            email.addBcc(StringUtils.split(bcc, ","));
        }
        email.addTo(conf.getTos());
        email.setSubject(conf.getSubject());
        email.setSocketConnectionTimeout(600000);
        email.setSocketTimeout(600000);
    }

    @Test
    public void testPortInformationIsGivenToHtmlEmailCorrectlyWithNoneProtocol() {
        conf.setSecurityProtocol(MailSecurityProtocolType.None);
        conf.setPort("535353");
        HtmlEmail mockedEmail = Mockito.mock(HtmlEmail.class);

        adapter.adapt(conf, mockedEmail);

        Mockito.verify(mockedEmail).setSmtpPort(535353);
    }

    @Test
    public void testPortInformationIsGivenToHtmlEmailCorrectlyWithSSLProtocol() {
        conf.setSecurityProtocol(MailSecurityProtocolType.SSL);
        conf.setPort("535353");
        HtmlEmail mockedEmail = Mockito.mock(HtmlEmail.class);

        adapter.adapt(conf, mockedEmail);

        Mockito.verify(mockedEmail).setSslSmtpPort("535353");
        Mockito.verify(mockedEmail).setSSLOnConnect(true);
    }

    @Test
    public void testPortInformationIsGivenToHtmlEmailCorrectlyWithTLSProtocol() {
        conf.setSecurityProtocol(MailSecurityProtocolType.TLS);
        conf.setPort("535353");
        HtmlEmail mockedEmail = Mockito.mock(HtmlEmail.class);

        adapter.adapt(conf, mockedEmail);

        Mockito.verify(mockedEmail).setSmtpPort(535353);
        Mockito.verify(mockedEmail).setStartTLSEnabled(true);
    }
}
