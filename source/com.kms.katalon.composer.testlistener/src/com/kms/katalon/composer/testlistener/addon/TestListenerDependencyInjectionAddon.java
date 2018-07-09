package com.kms.katalon.composer.testlistener.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.testlistener.handler.OpenTestListenerHandler;
import com.kms.katalon.composer.testlistener.handler.RenameTestListenerHandler;

public class TestListenerDependencyInjectionAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenTestListenerHandler.class, context);
        ContextInjectionFactory.make(RenameTestListenerHandler.class, context);
    }
}
