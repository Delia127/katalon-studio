package com.kms.katalon.activation.plugin;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MiscBundleActivator implements BundleActivator {

    private IEventBroker eventBroker;

    private static MiscBundleActivator instance;

    public static MiscBundleActivator getInstance() {
        return instance;
    }

    public IEventBroker getEventBroker() {
        return eventBroker;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        instance = this;

        IEclipseContext bundleEclipseContext = EclipseContextFactory.getServiceContext(context);

        eventBroker = bundleEclipseContext.get(IEventBroker.class);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
