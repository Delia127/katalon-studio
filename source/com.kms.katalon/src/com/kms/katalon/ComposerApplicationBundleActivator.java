package com.kms.katalon;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kms.katalon.application.ApplicationStarter;
import com.kms.katalon.core.application.WorkbenchApplicationStarter;

public class ComposerApplicationBundleActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(context);
        ApplicationStarter applicationStarter = ContextInjectionFactory
                .make(WorkbenchApplicationStarter.class, eclipseContext);
        context.registerService(ApplicationStarter.class, applicationStarter, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        
    }

}
