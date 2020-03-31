package com.kms.katalon.logging;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;

public class Activator implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;

        LogManager.active();
        if (isKSRE()) {
            LogManager.enableIgnoreError();
            IEclipseContext bundleEclipseContext = EclipseContextFactory.getServiceContext(context);

            IEventBroker eventBroker = bundleEclipseContext.get(IEventBroker.class);
            eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {

                @Override
                public void handleEvent(Event event) {
                    LogManager.disableIgnoreError();
                }
                
            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
        
        LogManager.stop();
    }

    private boolean isKSRE() {
        IProduct product = Platform.getProduct();
        return product != null && "com.kms.katalon.console.product".equals(product.getId());
    }
}
