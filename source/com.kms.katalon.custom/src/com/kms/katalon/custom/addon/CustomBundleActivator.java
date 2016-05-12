package com.kms.katalon.custom.addon;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kms.katalon.custom.handler.EvaluateIntegrationContributionsHandler;
import com.kms.katalon.custom.handler.EvaluateKeywordContributionsHandler;

public class CustomBundleActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(context);
        ContextInjectionFactory.make(EvaluateKeywordContributionsHandler.class, eclipseContext);
        ContextInjectionFactory.make(EvaluateIntegrationContributionsHandler.class, eclipseContext);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Do nothing here
    }
}
