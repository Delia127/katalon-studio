package com.kms.katalon.execution.integration;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

import com.kms.katalon.custom.contribution.IntegrationContributor;

public class EvaluateReportIntegrationContribution {
	private static final String REPORT_INTEGRATION_CONTRIBUTOR_ID = "com.kms.katalon.execution.integration";

	@Inject
	public void execute(IExtensionRegistry registry) {
		evaluate(registry);
	}

	private void evaluate(IExtensionRegistry registry) {
		IConfigurationElement[] config = registry.getConfigurationElementsFor(REPORT_INTEGRATION_CONTRIBUTOR_ID);
		try {
			for (IConfigurationElement e : config) {
				IntegrationContributor integrationContributor = (IntegrationContributor) e
						.createExecutableExtension("product");
				String productName = integrationContributor.getProductName();
				ReportIntegrationContribution reportContributor = (ReportIntegrationContribution) e
						.createExecutableExtension("report");
				ReportIntegrationFactory.getInstance().addNewReportIntegration(productName, reportContributor);
			}
		} catch (CoreException ex) {
		}
	}
}
