package com.kms.katalon.execution.mobile.driver;

import java.util.Map;

import com.kms.katalon.execution.entity.AbstractDriverConnector;

public abstract class AbstractMobileDriverConnector extends AbstractDriverConnector {
	protected String deviceName;

	public AbstractMobileDriverConnector(String deviceName) {
		this.deviceName = deviceName;
	}

	@Override
	public Map<String, String> getPropertyMap() {
		Map<String, String> propertyMap = super.getPropertyMap();
		propertyMap.put(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME, deviceName);
		return propertyMap;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

}
