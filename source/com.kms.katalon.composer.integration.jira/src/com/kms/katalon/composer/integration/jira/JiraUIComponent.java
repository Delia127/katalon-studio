package com.kms.katalon.composer.integration.jira;

import java.io.IOException;

import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.jira.JiraCredential;
import com.kms.katalon.integration.jira.setting.JiraIntegrationSettingStore;

public interface JiraUIComponent {

    default JiraIntegrationSettingStore getSettingStore() {
        return new JiraIntegrationSettingStore(ProjectController.getInstance().getCurrentProject().getFolderLocation());
    }

    default String getHTMLIssueURLPrefix() throws IOException {
        return getSettingStore().getServerUrl() + StringConstants.HREF_BROWSE_ISSUE;
    }

    default JiraCredential getCredential() throws IOException {
       return getSettingStore().getJiraCredential();
    }
}
