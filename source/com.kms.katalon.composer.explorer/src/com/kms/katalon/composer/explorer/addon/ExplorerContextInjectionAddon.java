package com.kms.katalon.composer.explorer.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.explorer.handlers.deletion.DeleteEntityHandlerRegister;


public class ExplorerContextInjectionAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(DeleteEntityHandlerRegister.class, context);
    }
}
