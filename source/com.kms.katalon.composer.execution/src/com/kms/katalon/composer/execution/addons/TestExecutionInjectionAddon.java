package com.kms.katalon.composer.execution.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.execution.collection.handler.CollectTestSuiteExecutionContribution;

public class TestExecutionInjectionAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(CollectTestSuiteExecutionContribution.class, context);
    }
}
