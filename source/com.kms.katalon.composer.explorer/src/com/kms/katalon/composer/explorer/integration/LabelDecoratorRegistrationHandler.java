package com.kms.katalon.composer.explorer.integration;

import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.custom.contribution.IntegrationContributor;

public class LabelDecoratorRegistrationHandler {
    private static final String LABEL_DECORATOR_CONTRIBUTOR_ID = "com.kms.katalon.composer.explorer.label";

    @Inject
    private IEclipseContext context;

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(LABEL_DECORATOR_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                IntegrationContributor contributor = (IntegrationContributor) e.createExecutableExtension("product");
                IntegrationLabelDecorator decorator = (IntegrationLabelDecorator) e
                        .createExecutableExtension("decorator");
                LabelDecoratorManager.getInstance().addDecorator(contributor.getProductName(), decorator);
                ContextInjectionFactory.inject(decorator, context);
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }
}
