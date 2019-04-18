package com.kms.katalon.composer.project.addon;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.kms.katalon.composer.project.keyword.ActionProviderFactory;
import com.kms.katalon.core.keyword.IActionProvider;

public class ProjectComposerBundleActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof IActionProvider) {
                    ServiceReference<IActionProvider> serviceReference = context
                            .getServiceReference(IActionProvider.class);
                    ActionProviderFactory.getInstance().setActionProvider(context.getService(serviceReference));
                    context.removeServiceListener(this);
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Nothing here yet
    }

}
