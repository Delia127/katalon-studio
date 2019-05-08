package com.kms.katalon.composer.explorer.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.explorer.handlers.OpenUserFileHandler;
import com.kms.katalon.composer.explorer.handlers.RenameUserFileEntityHandler;
import com.kms.katalon.composer.explorer.handlers.ReskinTextAreaHandler;
import com.kms.katalon.composer.explorer.handlers.deletion.DeleteEntityHandlerRegister;
import com.kms.katalon.composer.explorer.integration.LabelDecoratorRegistrationHandler;


public class ExplorerContextInjectionAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(DeleteEntityHandlerRegister.class, context);
        ContextInjectionFactory.make(ReskinTextAreaHandler.class, context);
        ContextInjectionFactory.make(LabelDecoratorRegistrationHandler.class, context);
        ContextInjectionFactory.make(OpenUserFileHandler.class, context);
        ContextInjectionFactory.make(RenameUserFileEntityHandler.class, context);
    }
}
