package com.kms.katalon.integration.jira.issue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.constant.StringConstants;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.setting.JiraIntegrationSettingStore;

public class EditIssueHTMLLinkProvider extends DefaultIssueHTMLLinkProvider {
    private JiraIssue currentIssue;
    
    public EditIssueHTMLLinkProvider(TestCaseLogRecord logRecord, JiraIntegrationSettingStore settingStore,
            JiraIssue jiraIssue) {
        super(logRecord, settingStore);
        this.currentIssue = jiraIssue;
    }

    @Override
    public String getIssueUrl() throws IOException {
        return settingStore.getServerUrl() + StringConstants.HREF_EDIT_ISSUE;
    }

    @Override
    public List<NameValuePair> getIssueParameters() throws IOException {
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_ID, Long.toString(currentIssue.getId())));
        return pairs;
    }

    @Override
    public String getIssueUrlPrefix() throws IOException, URISyntaxException {
        return settingStore.getServerUrl() + StringConstants.HREF_EDIT_ISSUE_PREFIX;
    }
}
