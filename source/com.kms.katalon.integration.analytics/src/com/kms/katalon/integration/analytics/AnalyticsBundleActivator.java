package com.kms.katalon.integration.analytics;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kms.katalon.feature.TestOpsFeatureActivator;
import com.kms.katalon.integration.analytics.providers.TestOpsFeatureActivatorImpl;


public class AnalyticsBundleActivator implements BundleActivator {

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        TestOpsFeatureActivator testOpsFeatureActivator = ContextInjectionFactory
                .make(TestOpsFeatureActivatorImpl.class, eclipseContext);
        bundleContext.registerService(TestOpsFeatureActivator.class, testOpsFeatureActivator, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
