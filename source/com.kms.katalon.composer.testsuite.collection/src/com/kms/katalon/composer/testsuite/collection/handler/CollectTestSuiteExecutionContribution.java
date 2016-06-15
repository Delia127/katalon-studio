package com.kms.katalon.composer.testsuite.collection.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.collection.execution.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionGroup;

public class CollectTestSuiteExecutionContribution {
    private static final String ATTRIBUTE_NAME = "item";

    private static final String CONTRIBUTOR_ID = "com.kms.katalon.composer.testsuite.collection.testSuiteExecutionItem";

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                final Object o = e.createExecutableExtension(ATTRIBUTE_NAME);
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
                LoggerSingleton.logError(e);
            }

            @Override
            public void run() throws Exception {
                if (o instanceof TestExecutionGroup) {
                    TestExecutionGroup contributor = (TestExecutionGroup) o;
                    TestExecutionGroupCollector.getInstance().addGroup(contributor);
                }
            }
        };
        SafeRunner.run(runnable);
    }
}
