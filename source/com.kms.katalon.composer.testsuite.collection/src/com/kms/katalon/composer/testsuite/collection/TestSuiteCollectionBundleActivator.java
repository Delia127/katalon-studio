package com.kms.katalon.composer.testsuite.collection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.kms.katalon.composer.testsuite.collection.platform.PlatformTestSuiteCollectionUIViewBuilder;
import com.kms.katalon.composer.testsuite.collection.view.TestSuiteCollectionViewFactory;

public class TestSuiteCollectionBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof PlatformTestSuiteCollectionUIViewBuilder) {
                    ServiceReference<PlatformTestSuiteCollectionUIViewBuilder> serviceReference = context
                            .getServiceReference(PlatformTestSuiteCollectionUIViewBuilder.class);
                    TestSuiteCollectionViewFactory.getInstance().setPlatformBuilder(context.getService(serviceReference));
                }
            }
        });
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		
	}
	
}
