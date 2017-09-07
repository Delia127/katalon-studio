package com.kms.katalon.composer.mobile.execution.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.execution.testsuite.MobileExecutionIntegrationCollector;
import com.kms.katalon.composer.mobile.execution.testsuite.MobileIntegrationProvider;

public class MobileIntegrationExecutionHandler {
    private static final String MOBILE_INTEGRATION_CONTRIBUTOR_ID = "com.kms.katalon.composer.mobile.mobileDriverIntegration";

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(MOBILE_INTEGRATION_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                MobileIntegrationProvider executionProvider = (MobileIntegrationProvider) e
                        .createExecutableExtension("executionProvider");

                MobileExecutionIntegrationCollector.getInstance().addNewProvider(executionProvider);
            }
        } catch (CoreException ex) {
            LoggerSingleton.logError(ex);
        }
    }
}
