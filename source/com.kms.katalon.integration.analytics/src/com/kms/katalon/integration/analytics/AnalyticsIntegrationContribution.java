package com.kms.katalon.integration.analytics;

import com.kms.katalon.custom.contribution.IntegrationContributor;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;

public class AnalyticsIntegrationContribution implements IntegrationContributor {

    @Override
    public String getProductName() {
        return AnalyticsStringConstants.ANALYTICS_NAME;
    }
}
