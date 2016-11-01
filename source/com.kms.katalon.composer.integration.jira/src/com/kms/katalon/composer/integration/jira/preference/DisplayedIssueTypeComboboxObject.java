package com.kms.katalon.composer.integration.jira.preference;

import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.integration.jira.entity.JiraIssueType;
import com.kms.katalon.integration.jira.setting.StoredJiraObject;

public class DisplayedIssueTypeComboboxObject extends DisplayedComboboxObject<JiraIssueType> {

    public DisplayedIssueTypeComboboxObject(StoredJiraObject<JiraIssueType> storedObject) {
        super(storedObject);
    }

    @Override
    public int getPreferredIndex() {
        JiraIssueType[] jiraIssueTypes = getStoredObject().getJiraObjects();
        for (int index = 0; index < jiraIssueTypes.length; index++) {
            if (StringConstants.DF_ISSUE_TYPE_NAME.equalsIgnoreCase(jiraIssueTypes[index].getName())) {
                return index;
            }
        }
        return super.getPreferredIndex();
    }
}
