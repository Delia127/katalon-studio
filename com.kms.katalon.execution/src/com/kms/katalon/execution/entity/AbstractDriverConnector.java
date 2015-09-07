package com.kms.katalon.execution.entity;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDriverConnector implements IDriverConnector {
	@Override
	public Map<String, String> getPropertyMap() {
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put(getDriverType().getPropertyKey(), getDriverType().getPropertyValue());
		return propertyMap; 
	}
}
