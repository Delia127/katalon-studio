package com.kms.katalon.console;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kms.katalon.application.ApplicationStarter;
import com.kms.katalon.console.application.ConsoleApplicationStarter;

public class ConsoleBundleActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(context);
        ApplicationStarter consoleApplicationStarter = ContextInjectionFactory.make(ConsoleApplicationStarter.class,
                eclipseContext);
        context.registerService(ApplicationStarter.class, consoleApplicationStarter, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

}
