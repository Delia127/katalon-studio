package com.kms.katalon.composer.global.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.global.handler.OpenGlobalVariableHandler;
import com.kms.katalon.composer.global.handler.RenameExecutionProfileHandler;

public class GlobalDependencyInjectionAddon {

    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenGlobalVariableHandler.class, context);
        ContextInjectionFactory.make(RenameExecutionProfileHandler.class, context);
    }
}
