package com.kms.katalon.execution.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.execution.handler.EvaluateRunConfigurationContributionsHandler;
import com.kms.katalon.execution.integration.EvaluateReportIntegrationContribution;


public class ExecutionInjectionManagerAddon {
	@PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(EvaluateReportIntegrationContribution.class, context);
        ContextInjectionFactory.make(EvaluateRunConfigurationContributionsHandler.class, context);
    }
}
