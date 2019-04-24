package com.kms.katalon.composer.testsuite.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.view.builder.TestSuiteUIViewBuilder;
import com.kms.katalon.composer.testsuite.view.contributor.ViewContributor;
import com.kms.katalon.composer.view.TestSuiteViewFactory;

public class EvaluateTestSuiteContributionViewHandler {
    private static final String TESTSUITE_VIEW_CONTRIBUTOR_ID = "com.kms.katalon.composer.testsuite.view";

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(TESTSUITE_VIEW_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                ViewContributor viewContributor = (ViewContributor) e
                        .createExecutableExtension("product");
                String productName = viewContributor.getProductName();
                TestSuiteUIViewBuilder contributionView = (TestSuiteUIViewBuilder) e
                        .createExecutableExtension("view");
                TestSuiteViewFactory.getInstance().addNewView(productName, contributionView);
            }
        } catch (CoreException ex) {
            LoggerSingleton.logError(ex);
        }
    }

}
