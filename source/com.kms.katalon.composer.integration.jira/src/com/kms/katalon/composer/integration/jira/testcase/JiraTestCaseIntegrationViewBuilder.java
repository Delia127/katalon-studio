package com.kms.katalon.composer.integration.jira.testcase;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class JiraTestCaseIntegrationViewBuilder implements TestCaseIntegrationViewBuilder {

    @Override
    public AbstractTestCaseIntegrationView getIntegrationView(TestCaseEntity testCase, MPart mpart) {
        return new JiraTestCaseIntegrationView(testCase, mpart);
    }

    @Override
    public int preferredOrder() {
        return 1;
    }
}
