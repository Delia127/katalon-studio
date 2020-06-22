package com.kms.katalon.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.application.preference.ProjectSettingPreference;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.feature.TestOpsConfiguration;
import com.kms.katalon.feature.TestOpsFeatureActivator;
import com.kms.katalon.logging.LogUtil;

public class KatalonApplicationActivator implements BundleActivator {

    private static TestOpsFeatureActivator featureActivator;

    private static TestOpsConfiguration testOpsConfiguration;

    private Map<RunningMode, ApplicationStarter> applicationStarters = new HashMap<RunningMode, ApplicationStarter>();

    private static KatalonApplicationActivator instance;

    @Override
    public void start(BundleContext context) throws Exception {
        instance = this;

        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(context);
        IEventBroker eventBroker = eclipseContext.get(IEventBroker.class);
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                try {
                    new ProjectSettingPreference().addRecentProject(currentProject);
                } catch (IOException e) {
                    LogUtil.printAndLogError(e);
                }
            }
        });

        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                Object service = context.getService(event.getServiceReference());
                if (service instanceof TestOpsFeatureActivator) {
                    featureActivator = (TestOpsFeatureActivator) service;
                    context.removeServiceListener(this);
                }
            }
        });

        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                Object service = context.getService(event.getServiceReference());
                if (service instanceof TestOpsConfiguration) {
                    testOpsConfiguration = (TestOpsConfiguration) service;
                    context.removeServiceListener(this);
                }
            }
        });

        context.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof ApplicationStarter) {
                    ServiceReference<ApplicationStarter> serviceReference = context
                            .getServiceReference(ApplicationStarter.class);
                    if (serviceReference == null) {
                        return;
                    }
                    RunningMode runningMode = event.getServiceReference().getBundle().getSymbolicName().equals(
                            "com.kms.katalon") ? RunningMode.GUI : RunningMode.CONSOLE;
                    applicationStarters.put(runningMode, context.getService(serviceReference));
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    public static TestOpsFeatureActivator getFeatureActivator() {
        return featureActivator;
    }

    public static TestOpsConfiguration getTestOpsConfiguration() {
        return testOpsConfiguration;
    }

    public static KatalonApplicationActivator getInstance() {
        return instance;
    }

    public Map<RunningMode, ApplicationStarter> getApplicationStarters() {
        return applicationStarters;
    }
}
