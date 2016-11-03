package com.kms.katalon.integration.jira.entity;

import java.net.URI;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;

public class JiraIssue extends BasicIssue {

    public static final String FIELD_ID = "id";

    public static final String FIELD_PROJECT_ID = "pid";
    
    public static final String FIELD_PARENT_ISSUE_ID = "parentIssueId";

    public static final String FIELD_ISSUE_TYPE = "issuetype";

    public static final String FIELD_SUMMARY = "summary";

    public static final String FIELD_REPORTER = "reporter";

    public static final String FIELD_DESCRIPTION = "description";

    public static final String FIELD_ENVIRONMENT = "environment";

    public JiraIssue(URI self, String key, Long id) {
        super(self, key, id);
    }

    public Issue getFields() {
        return fields;
    }

    public void setFields(Issue fields) {
        this.fields = fields;
    }

    private Issue fields;
}
