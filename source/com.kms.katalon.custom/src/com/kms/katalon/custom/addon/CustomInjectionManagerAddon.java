package com.kms.katalon.custom.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.custom.handler.EvaluateIntegrationContributionsHandler;
import com.kms.katalon.custom.handler.EvaluateKeywordContributionsHandler;

public class CustomInjectionManagerAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(EvaluateKeywordContributionsHandler.class, context);
        ContextInjectionFactory.make(EvaluateIntegrationContributionsHandler.class, context);
    }
}
