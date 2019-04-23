package com.kms.katalon.composer.integration.jira.testcase;

import java.io.IOException;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class JiraTestCaseIntegrationViewBuilder implements TestCaseIntegrationViewBuilder, JiraUIComponent {

	@Override
	public AbstractTestCaseIntegrationView getIntegrationView(TestCaseEntity testCase, MPart mpart,
			SavableCompositePart parentPart) {
		return new JiraTestCaseIntegrationView(testCase, mpart);
	}

	@Override
	public String getName() {
		return "JIRA";
	}

	@Override
	public boolean isEnabled(ProjectEntity project) {
		try {
			return !isJiraPluginEnabled() && getSettingStore().isIntegrationEnabled();
		} catch (IOException e) {
			return false;
		}
	}
}
