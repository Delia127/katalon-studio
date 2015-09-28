package com.kms.katalon.composer.execution.addons;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;

public class TestExecutionAddon implements EventHandler {

	@PostConstruct
	public void initHandlers(IEventBroker eventBroker) {
		eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, this);
	}

	@Override
	public void handleEvent(Event event) {
		// init Debug context for workbench
		if (event.getTopic().equals(EventConstants.WORKSPACE_CREATED)) {
			try {
				Platform.getBundle("com.kms.katalon.composer.execution").start();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
