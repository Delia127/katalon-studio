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

public class NewSubtaskHTMLLinkProvider extends DefaultIssueHTMLLinkProvider {
    private JiraIssue parentIssue;

    public NewSubtaskHTMLLinkProvider(TestCaseLogRecord logRecord, JiraIntegrationSettingStore settingStore,
            JiraIssue parentIssue) {
        super(logRecord, settingStore);
        this.parentIssue = parentIssue;
    }

    public String getIssueUrl() throws IOException {
        return settingStore.getServerUrl() + StringConstants.HREF_CREATE_SUB_TASK_ISSUE;
    }
    
    @Override
    public String getIssueUrlPrefix() throws IOException, URISyntaxException {
        return settingStore.getServerUrl() + StringConstants.HREF_CREATE_SUB_TASK_ISSUE_PREFIX;
    }

    @Override
    public List<NameValuePair> getIssueParameters() throws IOException {
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_PARENT_ISSUE_ID, Long.toString(parentIssue.getId())));
        return pairs;
    }

}
