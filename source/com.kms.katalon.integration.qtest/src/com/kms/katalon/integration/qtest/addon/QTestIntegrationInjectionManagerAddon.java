package com.kms.katalon.integration.qtest.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.integration.qtest.handler.QTestIntegrationActivationCheckHandler;

public class QTestIntegrationInjectionManagerAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(QTestIntegrationActivationCheckHandler.class, context);
    }
}
