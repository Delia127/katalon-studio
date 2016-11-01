package com.kms.katalon.integration.jira;

import org.apache.commons.lang3.StringUtils;

public class JiraAPIURL {
    public static final String REST_API_V2 = "/rest/api/2/";

    public static final String REST_API_URL_USER = "myself";

    public static final String REST_API_URL_PROJECT = "project";

    public static final String REST_API_URL_ISSUE = "issue";

    public static final String REST_API_URL_ISSUE_TYPE = "issuetype";

    public static String removeLastSplash(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        String coppied = String.copyValueOf(s.toCharArray());
        while (coppied.endsWith("/")) {
            coppied = coppied.substring(0, coppied.length() - 1);
        }
        return coppied;
    }

    public static String getJiraAPIPrexfix(JiraCredential credential) {
        return removeLastSplash(credential.getServerUrl()) + REST_API_V2;
    }

    public static String getUserAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_USER;
    }

    public static String getProjectAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_PROJECT;
    }

    public static String getIssueAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_ISSUE;
    }

    public static String getIssueTypeAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_ISSUE_TYPE;
    }
}
