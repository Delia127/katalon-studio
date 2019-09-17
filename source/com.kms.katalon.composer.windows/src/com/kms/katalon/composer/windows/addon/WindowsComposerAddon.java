package com.kms.katalon.composer.windows.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.windows.handler.OpenWindowsElementHandler;
import com.kms.katalon.composer.windows.handler.RenameWindowsObjectHandler;

public class WindowsComposerAddon {

    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenWindowsElementHandler.class, context);
        ContextInjectionFactory.make(RenameWindowsObjectHandler.class, context);
    }
}
