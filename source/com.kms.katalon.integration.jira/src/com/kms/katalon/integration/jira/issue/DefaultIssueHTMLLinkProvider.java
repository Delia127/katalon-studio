package com.kms.katalon.integration.jira.issue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.constant.StringConstants;
import com.kms.katalon.integration.jira.setting.JiraIntegrationSettingStore;

public abstract class DefaultIssueHTMLLinkProvider implements IssueHTMLLinkProvider {

    protected IssueMetaDataProvider issueMetaData;

    public IssueMetaDataProvider getIssueMetaData() {
        return issueMetaData;
    }

    protected JiraIntegrationSettingStore settingStore;

    protected DefaultIssueHTMLLinkProvider(TestCaseLogRecord logRecord, JiraIntegrationSettingStore settingStore) {
        this.settingStore = settingStore;
        this.issueMetaData = new IssueMetaDataProvider(logRecord);
    }

    protected DefaultIssueHTMLLinkProvider(TestCaseLogRecord logRecord, int endStepIndex,
            JiraIntegrationSettingStore settingStore) {
        this.settingStore = settingStore;
        this.issueMetaData = new IssueMetaDataProvider(logRecord, endStepIndex);
    }

    @Override
    public final String getHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return new URIBuilder(getIssueUrl()).addParameters(getIssueParameters()).build().toString();
    }

    protected List<NameValuePair> getIssueParameters() throws IOException {
        return Collections.emptyList();
    }

    @Override
    public String getLoginHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_LOGIN;
    }

    @Override
    public String getDashboardHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_DASHBOARD;
    }
    
    public String getSecureDashboardHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_SECURE;
    }
    
    public boolean isDashboard(String url) throws IOException, URISyntaxException, GeneralSecurityException {
    	return  url.startsWith(getSecureDashboardHTMLLink());
    }
}
