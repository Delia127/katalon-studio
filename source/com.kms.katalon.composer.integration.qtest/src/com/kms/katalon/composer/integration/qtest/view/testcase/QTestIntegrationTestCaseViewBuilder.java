package com.kms.katalon.composer.integration.qtest.view.testcase;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class QTestIntegrationTestCaseViewBuilder implements TestCaseIntegrationViewBuilder {

	@Override
	public AbstractTestCaseIntegrationView getIntegrationView(TestCaseEntity testCase, MPart mpart) {
		return new QTestIntegrationTestCaseView(testCase, mpart);
	}
	
}
