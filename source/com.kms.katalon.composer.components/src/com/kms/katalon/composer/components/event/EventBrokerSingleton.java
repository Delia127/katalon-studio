package com.kms.katalon.composer.components.event;

import org.eclipse.e4.core.services.events.IEventBroker;

public class EventBrokerSingleton {
private static EventBrokerSingleton _instance;
	private IEventBroker eventBroker;
	
	public static EventBrokerSingleton getInstance() {
		if (_instance == null) {
			_instance = new EventBrokerSingleton();
		}
		return _instance;
	}

	public IEventBroker getEventBroker() {
		return eventBroker;
	}

	public void setEventBroker(IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
	}
}
