package com.kms.katalon.integration.jira;

import com.kms.katalon.custom.contribution.IntegrationContributor;
import com.kms.katalon.integration.jira.constant.StringConstants;

public class JiraIntegrationContribution implements IntegrationContributor {

    @Override
    public String getProductName() {
        return StringConstants.JIRA_NAME;
    }

}
