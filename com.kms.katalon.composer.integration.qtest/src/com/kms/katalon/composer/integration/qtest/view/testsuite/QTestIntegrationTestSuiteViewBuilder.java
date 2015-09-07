package com.kms.katalon.composer.integration.qtest.view.testsuite;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.testsuite.parts.integration.AbstractTestSuiteIntegrationView;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationViewBuilder;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class QTestIntegrationTestSuiteViewBuilder implements TestSuiteIntegrationViewBuilder{

	@Override
	public AbstractTestSuiteIntegrationView getIntegrationView(TestSuiteEntity testSuite, MPart mpart) {
		return new QTestIntegrationTestSuiteView(testSuite, mpart);
	}
}
