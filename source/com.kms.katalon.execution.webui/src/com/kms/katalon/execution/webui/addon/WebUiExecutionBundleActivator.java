package com.kms.katalon.execution.webui.addon;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.keyword.IActionProvider;
import com.kms.katalon.execution.webui.keyword.ActionProviderImpl;

public class WebUiExecutionBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		IEclipseContext bundleEclipseContext = EclipseContextFactory.getServiceContext(context);
		IEventBroker eventBroker = bundleEclipseContext.get(IEventBroker.class);

		eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
            	IActionProvider customKeywordPluginActionProvider = ContextInjectionFactory
            			.make(ActionProviderImpl.class, bundleEclipseContext);
            	context.registerService(IActionProvider.class, customKeywordPluginActionProvider,
                        null);
            }
        });
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// Nothing here yet
	}

}
