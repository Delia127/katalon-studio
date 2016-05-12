package com.kms.katalon.execution.addon;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kms.katalon.execution.handler.EvaluateDriverConnectorContributionsHandler;
import com.kms.katalon.execution.handler.EvaluateRunConfigurationContributionsHandler;
import com.kms.katalon.execution.integration.EvaluateReportIntegrationContribution;

public class ExecutionBundleActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(context);
        ContextInjectionFactory.make(EvaluateReportIntegrationContribution.class, eclipseContext);
        ContextInjectionFactory.make(EvaluateRunConfigurationContributionsHandler.class, eclipseContext);
        ContextInjectionFactory.make(EvaluateDriverConnectorContributionsHandler.class, eclipseContext);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Do nothing here
    }
}
