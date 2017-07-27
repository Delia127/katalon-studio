package com.kms.katalon.composer.testcase.parts.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.entity.testcase.TestCaseEntity;

public interface TestCaseIntegrationViewBuilder {
	public AbstractTestCaseIntegrationView getIntegrationView(TestCaseEntity testCase, MPart mpart);
	
	public int preferredOrder();
}
