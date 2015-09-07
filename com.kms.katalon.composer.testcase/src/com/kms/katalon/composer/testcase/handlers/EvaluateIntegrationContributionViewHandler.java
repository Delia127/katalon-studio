package com.kms.katalon.composer.testcase.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.integration.TestCaseIntegrationFactory;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.custom.contribution.IntegrationContributor;

public class EvaluateIntegrationContributionViewHandler {
	private static final String TESTCASE_INTEGRATION_CONTRIBUTOR_ID = "com.kms.katalon.composer.testcase.integration";

	@Inject
	public void execute(IExtensionRegistry registry) {
		evaluate(registry);
	}

	private void evaluate(IExtensionRegistry registry) {
		IConfigurationElement[] config = registry.getConfigurationElementsFor(TESTCASE_INTEGRATION_CONTRIBUTOR_ID);
		try {
			for (IConfigurationElement e : config) {
				IntegrationContributor integrationContributor = (IntegrationContributor) e.createExecutableExtension("product");
				String productName = integrationContributor.getProductName();
				TestCaseIntegrationViewBuilder contributionView = (TestCaseIntegrationViewBuilder) e.createExecutableExtension("view");
				TestCaseIntegrationFactory.getInstance().addNewIntegrationView(productName, contributionView);

			}
		} catch (CoreException ex) {
			LoggerSingleton.logError(ex);
		}
	}
}
