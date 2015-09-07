package com.kms.katalon.composer.integration.qtest;

import com.kms.katalon.custom.contribution.IntegrationContributor;
import com.kms.katalon.integration.qtest.QTestConstants;

public class QTestIntegrationContribution implements IntegrationContributor {

	@Override
	public String getProductName() {
		return QTestConstants.PRODUCT_NAME;
	}

}
