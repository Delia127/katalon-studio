package com.kms.katalon.composer.components.services;

import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class PartServiceSingleton {
	private static PartServiceSingleton _instance;

	private EPartService partService;

	public static PartServiceSingleton getInstance() {
		if (_instance == null) {
			_instance = new PartServiceSingleton();
		}
		return _instance;
	}

	public EPartService getPartService() {
		return partService;
	}

	public void setPartService(EPartService partService) {
		this.partService = partService;
	}
}
