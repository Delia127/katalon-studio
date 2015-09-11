package com.kms.katalon.composer.components.log;

import org.eclipse.e4.core.services.log.Logger;

@SuppressWarnings("restriction")
public class LoggerSingleton {
	private static LoggerSingleton _instance;
	
	private Logger logger;
	
	public static LoggerSingleton getInstance() {
		if (_instance == null) {
			_instance = new LoggerSingleton();
		}
		return _instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public static void logError(Throwable e) {
	    getInstance().getLogger().error(e);
	}
	
	public static void logError(Throwable e, String msg) {
		getInstance().getLogger().error(e, msg);
	}
}
