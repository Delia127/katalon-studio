package com.kms.katalon.composer.components.application;

import org.eclipse.e4.ui.model.application.MApplication;

public class ApplicationSingleton {
	private static ApplicationSingleton _instance;

	private MApplication application;

	public static ApplicationSingleton getInstance() {
		if (_instance == null) {
			_instance = new ApplicationSingleton();
		}
		return _instance;
	}

	public MApplication getApplication() {
		return application;
	}

	public void setApplication(MApplication application) {
		this.application = application;
	}
}
