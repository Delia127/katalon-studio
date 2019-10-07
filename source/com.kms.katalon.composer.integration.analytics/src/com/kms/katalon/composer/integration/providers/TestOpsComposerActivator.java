package com.kms.katalon.composer.integration.providers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kms.katalon.feature.TestOpsConfiguration;

public class TestOpsComposerActivator implements BundleActivator{
	
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        TestOpsConfiguration testOpsConfiguration = ContextInjectionFactory
                .make(TestOpsConfigurationImpl.class, eclipseContext);
        bundleContext.registerService(TestOpsConfiguration.class, testOpsConfiguration, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        //Do nothing here
    }
}
