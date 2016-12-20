package com.kms.katalon.custom.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;

public class EvaluateKeywordContributionsHandler {
    private static final String IKEYWORD_CONTRIBUTOR_ID = "com.kms.katalon.custom.keyword";
    
    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(IKEYWORD_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                final Object o = e.createExecutableExtension("class");
                    executeExtension(o);
            }
        } catch (CoreException ex) {
            
        }
    }

    private void executeExtension(final Object o) {
        ISafeRunnable runnable = new ISafeRunnable() {
            @Override
            public void handleException(Throwable e) {
            }

            @Override
            public void run() throws Exception {
                if (o instanceof IKeywordContributor) {
                    KeywordContributorCollection.addKeywordContributor((IKeywordContributor) o);
                }
            }
        };
        SafeRunner.run(runnable);
    }
}
