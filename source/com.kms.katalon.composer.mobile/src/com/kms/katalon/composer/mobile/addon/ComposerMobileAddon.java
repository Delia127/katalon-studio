package com.kms.katalon.composer.mobile.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.mobile.execution.handler.MobileIntegrationExecutionHandler;
import com.kms.katalon.composer.mobile.handler.OpenMobileTestObjectHandler;


public class ComposerMobileAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(MobileIntegrationExecutionHandler.class, context);
        ContextInjectionFactory.make(OpenMobileTestObjectHandler.class, context);
    }
}
