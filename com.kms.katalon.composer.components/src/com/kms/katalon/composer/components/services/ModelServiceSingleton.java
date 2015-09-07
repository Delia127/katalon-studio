package com.kms.katalon.composer.components.services;

import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class ModelServiceSingleton {
	private static ModelServiceSingleton _instance;

	private EModelService modelService;

	public EModelService getModelService() {
		return modelService;
	}

	public void setModelService(EModelService modelService) {
		this.modelService = modelService;
	}

	public static ModelServiceSingleton getInstance() {
		if (_instance == null) {
			_instance = new ModelServiceSingleton();
		}
		return _instance;
	}
}
