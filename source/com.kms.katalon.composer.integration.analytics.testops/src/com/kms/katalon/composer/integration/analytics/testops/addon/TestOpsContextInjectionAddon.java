package com.kms.katalon.composer.integration.analytics.testops.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.integration.analytics.testops.handlers.OpenTestOpsHandler;

public class TestOpsContextInjectionAddon {
	@PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenTestOpsHandler.class, context);
    }

}
