package com.kms.katalon.execution.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import com.kms.katalon.execution.collector.ExecutionPropertiesCollector;
import com.kms.katalon.execution.configuration.contributor.IExecutionPropertiesContributor;
import com.kms.katalon.logging.LogUtil;

public class EvaluateExecutionPropertiesContributionHandler {
    
    private static final String IEXECUTIONPROPERTIES_ATTRIBUTE_NAME = "execution";
    private static final String IEXECUTIONPROPERTIES_CONTRIBUTOR_ID = "com.kms.katalon.execution.executionProperties";

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(IEXECUTIONPROPERTIES_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                final Object o = e.createExecutableExtension(IEXECUTIONPROPERTIES_ATTRIBUTE_NAME);
                executeExtension(o);
            }
        } catch (CoreException ex) {
            LogUtil.logError(ex);
        }
    }

    private void executeExtension(final Object o) {
        ISafeRunnable runnable = new ISafeRunnable() {
            @Override
            public void handleException(Throwable e) {
            }

            @Override
            public void run() throws Exception {
                if (o instanceof IExecutionPropertiesContributor) {
                    IExecutionPropertiesContributor contributor = (IExecutionPropertiesContributor) o;
                    ExecutionPropertiesCollector.getInstance().addContributor(contributor);
                }
            }
        };
        SafeRunner.run(runnable);
    }

}
