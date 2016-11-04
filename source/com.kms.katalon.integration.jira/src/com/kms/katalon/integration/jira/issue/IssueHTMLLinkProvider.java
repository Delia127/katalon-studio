package com.kms.katalon.integration.jira.issue;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IssueHTMLLinkProvider {
    String getLoginHTMLLink() throws IOException, URISyntaxException;

    String getHTMLLink() throws IOException, URISyntaxException;
    
    String getDashboardHTMLLink() throws IOException, URISyntaxException;
    
    String getIssueUrl() throws IOException, URISyntaxException;
    
    String getIssueUrlPrefix() throws IOException, URISyntaxException;
}
