package com.kms.katalon.composer.testsuite;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.kms.katalon.composer.testsuite.integration.TestSuiteIntegrationFactory;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationPlatformBuilder;

public class TestSuiteComposerActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof TestSuiteIntegrationPlatformBuilder) {
                    ServiceReference<TestSuiteIntegrationPlatformBuilder> serviceReference = context
                            .getServiceReference(TestSuiteIntegrationPlatformBuilder.class);
                    TestSuiteIntegrationFactory.getInstance().setPlatformBuilder(context.getService(serviceReference));
                    context.removeServiceListener(this);
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
