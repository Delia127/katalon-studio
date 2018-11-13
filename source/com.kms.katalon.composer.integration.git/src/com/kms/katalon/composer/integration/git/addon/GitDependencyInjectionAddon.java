package com.kms.katalon.composer.integration.git.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.integration.git.handlers.CloneRemoteProjectHandler;
import com.kms.katalon.composer.integration.git.handlers.DisconnectProjectHandler;

public class GitDependencyInjectionAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(CloneRemoteProjectHandler.class, context);
        ContextInjectionFactory.make(DisconnectProjectHandler.class, context);
    }
}
