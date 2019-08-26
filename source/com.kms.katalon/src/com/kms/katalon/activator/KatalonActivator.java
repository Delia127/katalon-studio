package com.kms.katalon.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.FeatureServiceConsumer;

public class KatalonActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        // Listening for FeatureService registration
        context.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof IFeatureService) {
                    ServiceReference<IFeatureService> serviceReference = context
                            .getServiceReference(IFeatureService.class);
                    FeatureServiceConsumer.setFeatureService(context.getService(serviceReference));
                    context.removeServiceListener(this);
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Do nothing here
    }

}
