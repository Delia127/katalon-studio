package com.kms.katalon.composer.testsuite.parts.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface TestSuiteIntegrationViewBuilder {
	public AbstractTestSuiteIntegrationView getIntegrationView(TestSuiteEntity testSuite, MPart mpart);
}
