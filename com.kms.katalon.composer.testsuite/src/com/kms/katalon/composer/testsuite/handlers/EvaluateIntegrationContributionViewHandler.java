package com.kms.katalon.composer.testsuite.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.integration.TestSuiteIntegrationFactory;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationViewBuilder;
import com.kms.katalon.custom.contribution.IntegrationContributor;

public class EvaluateIntegrationContributionViewHandler {
	private static final String TESTSUITE_INTEGRATION_CONTRIBUTOR_ID = "com.kms.katalon.composer.testsuite.integration";

	@Inject
	public void execute(IExtensionRegistry registry) {
		evaluate(registry);
	}

	private void evaluate(IExtensionRegistry registry) {
		IConfigurationElement[] config = registry.getConfigurationElementsFor(TESTSUITE_INTEGRATION_CONTRIBUTOR_ID);
		try {
			for (IConfigurationElement e : config) {
				IntegrationContributor integrationContributor = (IntegrationContributor) e
						.createExecutableExtension("product");
				String productName = integrationContributor.getProductName();
				TestSuiteIntegrationViewBuilder contributionView = (TestSuiteIntegrationViewBuilder) e
						.createExecutableExtension("view");
				TestSuiteIntegrationFactory.getInstance().addNewIntegrationView(productName, contributionView);

			}
		} catch (CoreException ex) {
			LoggerSingleton.logError(ex);
		}
	}
}
