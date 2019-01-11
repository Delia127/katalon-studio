package com.kms.katalon.platform.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
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

import com.kms.katalon.composer.report.platform.PlatformReportIntegrationViewBuilder;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationPlatformBuilder;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.platform.internal.event.ProjectEventPublisher;
import com.kms.katalon.platform.internal.report.ReportIntegrationPlatformBuilderImpl;
import com.kms.katalon.platform.internal.testcase.TestCaseIntegrationPlatformBuilderImpl;

public class InternalPlatformPlugin implements BundleActivator {

    private List<InternalPlatformService> platformServices = new ArrayList<>();

    @Override
    public void start(BundleContext context) throws Exception {
        activatePlatform(context);

        platformServices.forEach(service -> service.onPostConstruct());
    }

    private void activatePlatform(BundleContext context) throws BundleException {
        IEclipseContext modelContext = EclipseContextFactory.getServiceContext(context);

        Bundle bundle = Platform.getBundle("com.katalon.platform");
        bundle.start();

        IEventBroker eventBroker = modelContext.get(IEventBroker.class);

        PlatformServiceProvider platformServiceProvider = PlatformServiceProvider.getInstance();
        eventBroker.post("KATALON_PLUGIN/CONTROLLER_MANAGER_ADDED", platformServiceProvider.getControllerManager());

        eventBroker.post("KATALON_PLUGIN/UISERVICE_MANAGER_ADDED", platformServiceProvider.getUiServiceManager());
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                IEclipseContext eclipseContext = PlatformUI.getWorkbench().getService(IEclipseContext.class);
                BundleContext bundleContext = bundle.getBundleContext();
                bundleContext.registerService(IEclipseContext.class, eclipseContext, null);

                TestCaseIntegrationPlatformBuilder testCaseIntegrationViewBuilder = ContextInjectionFactory
                        .make(TestCaseIntegrationPlatformBuilderImpl.class, eclipseContext);
                bundleContext.registerService(TestCaseIntegrationPlatformBuilder.class, testCaseIntegrationViewBuilder,
                        null);
                
                PlatformReportIntegrationViewBuilder reportIntegrationViewBuilder = ContextInjectionFactory
                        .make(ReportIntegrationPlatformBuilderImpl.class, eclipseContext);
                bundleContext.registerService(PlatformReportIntegrationViewBuilder.class, reportIntegrationViewBuilder,
                        null);
            }
        });

        platformServices.add(new ProjectEventPublisher(eventBroker));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        platformServices.forEach(service -> service.onPreDestroy());
    }
}
