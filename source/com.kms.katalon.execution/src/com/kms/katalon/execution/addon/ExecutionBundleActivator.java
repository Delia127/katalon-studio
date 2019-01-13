package com.kms.katalon.execution.addon;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.kms.katalon.execution.console.LauncherOptionParserFactory;
import com.kms.katalon.execution.handler.EvaluateDriverConnectorContributionsHandler;
import com.kms.katalon.execution.handler.EvaluateRunConfigurationContributionsHandler;
import com.kms.katalon.execution.integration.EvaluateReportIntegrationContribution;
import com.kms.katalon.execution.platform.PlatformLauncherOptionParserBuilder;

public class ExecutionBundleActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(context);
        ContextInjectionFactory.make(EvaluateReportIntegrationContribution.class, eclipseContext);
        ContextInjectionFactory.make(EvaluateRunConfigurationContributionsHandler.class, eclipseContext);
        ContextInjectionFactory.make(EvaluateDriverConnectorContributionsHandler.class, eclipseContext);
        
        context.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                if (context.getService(event.getServiceReference()) instanceof PlatformLauncherOptionParserBuilder) {
                    ServiceReference<PlatformLauncherOptionParserBuilder> serviceReference = context
                            .getServiceReference(PlatformLauncherOptionParserBuilder.class);
                    LauncherOptionParserFactory.getInstance().setPlatformBuilder(context.getService(serviceReference));
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
