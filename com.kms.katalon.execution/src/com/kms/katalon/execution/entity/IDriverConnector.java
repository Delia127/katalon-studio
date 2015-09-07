package com.kms.katalon.execution.entity;

import java.util.Map;

import com.kms.katalon.core.driver.DriverType;

public interface IDriverConnector {
	public DriverType getDriverType();
	public Map<String, String> getPropertyMap();
}
