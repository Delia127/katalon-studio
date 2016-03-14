package com.kms.katalon.composer.integration.qtest;

import com.kms.katalon.custom.contribution.IntegrationContributor;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;

public class QTestIntegrationContribution implements IntegrationContributor {

    @Override
    public String getProductName() {
        return QTestStringConstants.PRODUCT_NAME;
    }

}
