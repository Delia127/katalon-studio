package com.kms.katalon.platform.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;

public class InternalPlatformPlugin implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        activatePlatform(context);
    }

    private void activatePlatform(BundleContext context) throws BundleException {
        IEclipseContext modelContext = EclipseContextFactory.getServiceContext(context);

        Bundle bundle = Platform.getBundle("com.katalon.platform");
        bundle.start();

        IEventBroker eventBroker = modelContext.get(IEventBroker.class);

        PlatformServiceProvider platformServiceProvider = PlatformServiceProvider.getInstance();
        eventBroker.post("KATALON_PLATFORM/CONTROLLER_MANAGER_ADDED",
                platformServiceProvider.getControllerManager());

        eventBroker.post("KATALON_PLATFORM/UISERVICE_MANAGER_ADDED",
                platformServiceProvider.getUiServiceManager());
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                IEclipseContext eclipseContext = PlatformUI.getWorkbench().getService(IEclipseContext.class);
                bundle.getBundleContext().registerService(IEclipseContext.class, eclipseContext, null);
            }
            
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
