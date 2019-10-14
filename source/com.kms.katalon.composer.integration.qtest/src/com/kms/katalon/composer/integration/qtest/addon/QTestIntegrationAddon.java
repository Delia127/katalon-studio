package com.kms.katalon.composer.integration.qtest.addon;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.integration.qtest.handler.UninstallQTestPluginHandler;

public class QTestIntegrationAddon {
    @Inject
    private IEclipseContext context;
    
    @PostConstruct
    public void registerHandler() {
        ContextInjectionFactory.make(UninstallQTestPluginHandler.class, context);
    }

}
