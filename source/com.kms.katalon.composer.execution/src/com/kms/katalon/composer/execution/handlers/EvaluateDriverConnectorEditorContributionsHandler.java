package com.kms.katalon.composer.execution.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.collector.DriverConnectorEditorCollector;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;

public class EvaluateDriverConnectorEditorContributionsHandler {
    private static final String IDRIVERCONNECTOR_EDITOR_ATTRIBUTE_NAME = "driver.connector.editor";
    private static final String IDRIVERCONNECTOR_EDITOR_CONTRIBUTOR_ID = "com.kms.katalon.composer.execution.driverConnectorEditor";

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(IDRIVERCONNECTOR_EDITOR_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                final Object o = e.createExecutableExtension(IDRIVERCONNECTOR_EDITOR_ATTRIBUTE_NAME);
                executeExtension(o);
            }
        } catch (CoreException ex) {
            LoggerSingleton.logError(ex);
        }
    }

    private void executeExtension(final Object o) {
        ISafeRunnable runnable = new ISafeRunnable() {
            @Override
            public void handleException(Throwable e) {
            }

            @Override
            public void run() throws Exception {
                if (o instanceof IDriverConnectorEditorContributor) {
                    DriverConnectorEditorCollector.getInstance().addDriverConnectorEditorContributor(
                            (IDriverConnectorEditorContributor) o);
                }
            }
        };
        SafeRunner.run(runnable);
    }
}
