package com.kms.katalon.composer.explorer.handlers.deletion;

import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class DeleteEntityHandlerRegister {
    private static final String DELETE_ENTITY_CONTRIBUTOR_ID = "com.kms.katalon.composer.explorer.delete";

    @Inject
    IEclipseContext context;

    @Inject
    public void execute(IExtensionRegistry registry) {
        evaluate(registry);
    }

    private void evaluate(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(DELETE_ENTITY_CONTRIBUTOR_ID);
        try {
            for (IConfigurationElement e : config) {
                IDeleteEntityHandler handler = (IDeleteEntityHandler) e.createExecutableExtension("handler");
                DeleteEntityHandlerFactory.getInstance().addContributor(handler);
                ContextInjectionFactory.inject(handler, context);
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            
        }
    }
}
