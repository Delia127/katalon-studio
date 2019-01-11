package com.kms.katalon.composer.testcase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.kms.katalon.composer.testcase.integration.TestCaseIntegrationFactory;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationPlatformBuilder;

public class TestCaseComposerActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof TestCaseIntegrationPlatformBuilder) {
                    ServiceReference<TestCaseIntegrationPlatformBuilder> serviceReference = context
                            .getServiceReference(TestCaseIntegrationPlatformBuilder.class);
                    TestCaseIntegrationFactory.getInstance().setPlatformBuilder(context.getService(serviceReference));
                    context.removeServiceListener(this);
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
