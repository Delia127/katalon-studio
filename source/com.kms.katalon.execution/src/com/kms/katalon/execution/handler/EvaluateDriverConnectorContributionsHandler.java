package com.kms.katalon.execution.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import com.kms.katalon.execution.collector.DriverConnectorCollector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;

public class EvaluateDriverConnectorContributionsHandler {
    private static final String IDRIVERCONNECTOR_ATTRIBUTE_NAME = "driver.connector";
    private static final String IDRIVERCONNECTOR_CONTRIBUTOR_ID = "com.kms.katalon.execution.driverConnector";

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(IDRIVERCONNECTOR_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                final Object o = e.createExecutableExtension(IDRIVERCONNECTOR_ATTRIBUTE_NAME);
                executeExtension(o);
            }
        } catch (CoreException ex) {
            // do nothing
        }
    }

    private void executeExtension(final Object o) {
        ISafeRunnable runnable = new ISafeRunnable() {
            @Override
            public void handleException(Throwable e) {
            }

            @Override
            public void run() throws Exception {
                if (o instanceof IDriverConnectorContributor) {
                    IDriverConnectorContributor contributor = (IDriverConnectorContributor) o;
                    DriverConnectorCollector.getInstance().addBuiltinDriverConnectorContributor(contributor);
                }
            }
        };
        SafeRunner.run(runnable);
    }
}
