package com.kms.katalon.composer.components.services;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

public class SelectionServiceSingleton {
	private static SelectionServiceSingleton _instance;

	private ESelectionService selectionService;

	public static SelectionServiceSingleton getInstance() {
		if (_instance == null) {
			_instance = new SelectionServiceSingleton();
		}
		return _instance;
	}

	public ESelectionService getSelectionService() {
		return selectionService;
	}

	public void setSelectionService(ESelectionService selectionService) {
		this.selectionService = selectionService;
	}
}
