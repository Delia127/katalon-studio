package com.kms.katalon.platform.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.katalon.platform.internal.console.LauncherOptionParserPlatformBuilderImpl;
import com.kms.katalon.execution.platform.PlatformLauncherOptionParserBuilder;
import com.kms.katalon.platform.internal.event.ProjectEventPublisher;

public class InternalPlatformPlugin implements BundleActivator {

    private List<InternalPlatformService> platformServices = new ArrayList<>();

    private IEventBroker eventBroker;

    private static InternalPlatformPlugin instance;

    @Override
    public void start(BundleContext context) throws Exception {
        instance = this;

        activatePlatform(context);

        platformServices.forEach(service -> service.onPostConstruct());
    }

    private void activatePlatform(BundleContext context) throws BundleException {
        IEclipseContext bundleEclipseContext = EclipseContextFactory.getServiceContext(context);

        Bundle bundle = Platform.getBundle("com.katalon.platform");
        bundle.start();

        eventBroker = bundleEclipseContext.get(IEventBroker.class);

        PlatformServiceProvider platformServiceProvider = PlatformServiceProvider.getInstance();
        eventBroker.post("KATALON_PLUGIN/CONTROLLER_MANAGER_ADDED", platformServiceProvider.getControllerManager());

        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundle.getBundleContext());
        BundleContext bundleContext = bundle.getBundleContext();
        PlatformLauncherOptionParserBuilder laucherOptionParserBuilder = ContextInjectionFactory
                .make(LauncherOptionParserPlatformBuilderImpl.class, eclipseContext);
        bundleContext.registerService(PlatformLauncherOptionParserBuilder.class, laucherOptionParserBuilder, null);

        platformServices.add(new ProjectEventPublisher(eventBroker));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        platformServices.forEach(service -> service.onPreDestroy());
    }

    public static InternalPlatformPlugin getInstance() {
        return instance;
    }

    public IEventBroker getEventBroker() {
        return eventBroker;
    }
}
