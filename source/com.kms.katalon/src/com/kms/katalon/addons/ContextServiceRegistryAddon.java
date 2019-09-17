package com.kms.katalon.addons;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.handlers.ActiveEventLogPartHandler;
import com.kms.katalon.composer.handlers.CheckForUpdateOnStartupHandler;
import com.kms.katalon.composer.handlers.InstallBasicReportPluginHandler;
import com.kms.katalon.composer.handlers.InstallComposerArtifactBundleHandler;
import com.kms.katalon.composer.handlers.ProjectToolbarHandler;


public class ContextServiceRegistryAddon {

    @Inject
    private IEclipseContext context;
    
    @PostConstruct
    public void registerHandlers() {
        ContextInjectionFactory.make(CheckForUpdateOnStartupHandler.class, context);
//        ContextInjectionFactory.make(ShowUserFeedbackDialogHandler.class, context);
        ContextInjectionFactory.make(ProjectToolbarHandler.class, context);
//        ContextInjectionFactory.make(ShowInAppSurveyDialogHandler.class, context);
        ContextInjectionFactory.make(ActiveEventLogPartHandler.class, context);
//        ContextInjectionFactory.make(InstallBasicReportPluginHandler.class, context);
        ContextInjectionFactory.make(InstallComposerArtifactBundleHandler.class, context);
    }
    
}
