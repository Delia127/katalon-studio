package com.kms.katalon.composer.testsuite;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.kms.katalon.composer.testsuite.platform.PlatformTestSuiteUIViewBuilder;
import com.kms.katalon.composer.view.TestSuiteViewFactory;

public class TestSuiteComposerActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof PlatformTestSuiteUIViewBuilder) {
                    ServiceReference<PlatformTestSuiteUIViewBuilder> serviceReference = context
                            .getServiceReference(PlatformTestSuiteUIViewBuilder.class);
                    TestSuiteViewFactory.getInstance().setPlatformBuilder(context.getService(serviceReference));
                    context.removeServiceListener(this);
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
