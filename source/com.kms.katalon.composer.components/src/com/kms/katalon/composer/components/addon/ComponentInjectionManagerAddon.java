package com.kms.katalon.composer.components.addon;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;

@SuppressWarnings("restriction")
public class ComponentInjectionManagerAddon {
	@Inject
	private Logger logger;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject 
	private ESelectionService selectionService;
	
	@Inject
	private EModelService modelService;
	
	@Inject
	private MApplication application;
	
	@Inject
	private UISynchronize sync;

	@PostConstruct
	public void initHandlers() {
		LoggerSingleton.getInstance().setLogger(logger);
		EventBrokerSingleton.getInstance().setEventBroker(eventBroker);
		SelectionServiceSingleton.getInstance().setSelectionService(selectionService);
		ModelServiceSingleton.getInstance().setModelService(modelService);
		ApplicationSingleton.getInstance().setApplication(application);
		UISynchronizeService.getInstance().setSync(sync);
	}
}
