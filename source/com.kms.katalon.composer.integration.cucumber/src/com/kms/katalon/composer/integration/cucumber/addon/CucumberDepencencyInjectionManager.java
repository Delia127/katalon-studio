package com.kms.katalon.composer.integration.cucumber.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.integration.cucumber.handler.OpenFeatureEntityHandler;
import com.kms.katalon.composer.integration.cucumber.handler.RenameFeatureEntityHandler;

public class CucumberDepencencyInjectionManager {

    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenFeatureEntityHandler.class, context);
        ContextInjectionFactory.make(RenameFeatureEntityHandler.class, context);
    }
}
