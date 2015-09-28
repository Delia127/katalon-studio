package com.kms.katalon.execution.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class EvaluateRunConfigurationContributionsHandler {
	private static final String IRUNCONFIGURATION_ATTRIBUTE_NAME = "configuration";
	private static final String IRUNCONFIGURATION_CONTRIBUTOR_ID = "com.kms.katalon.execution.runConfiguration";

	@Inject
	public void execute(IExtensionRegistry registry) {
		evaluate(registry);
	}

	private void evaluate(IExtensionRegistry registry) {
		IConfigurationElement[] config = registry.getConfigurationElementsFor(IRUNCONFIGURATION_CONTRIBUTOR_ID);
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension(IRUNCONFIGURATION_ATTRIBUTE_NAME);
				executeExtension(o);
			}
		} catch (CoreException ex) {
			// do nothing
		}
	}

	private void executeExtension(final Object o) {
		ISafeRunnable runnable = new ISafeRunnable() {
			@Override
			public void handleException(Throwable e) {
			}

			@Override
			public void run() throws Exception {
				if (o instanceof IRunConfigurationContributor) {
					IRunConfigurationContributor contributor = (IRunConfigurationContributor) o;
					RunConfigurationCollector.getInstance().addBuiltinRunConfigurationContributor(contributor);
				}
			}
		};
		SafeRunner.run(runnable);
	}
}
