package com.kms.katalon.composer.components.services;

import org.eclipse.e4.ui.di.UISynchronize;

public class UISynchronizeService {
	private static UISynchronizeService _instance;

	private UISynchronize sync;

	public static UISynchronizeService getInstance() {
		if (_instance == null) {
			_instance = new UISynchronizeService();
		}
		return _instance;
	}

	public UISynchronize getSync() {
		return sync;
	}

	public void setSync(UISynchronize sync) {
		this.sync = sync;
	}
}
