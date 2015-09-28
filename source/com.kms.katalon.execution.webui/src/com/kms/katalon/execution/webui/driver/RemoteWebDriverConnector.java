package com.kms.katalon.execution.webui.driver;

import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.entity.AbstractDriverConnector;

public class RemoteWebDriverConnector extends AbstractDriverConnector {
	private String remoteServerUrl;
	
	public RemoteWebDriverConnector(String remoteServerUrl) {
		this.setRemoteServerUrl(remoteServerUrl);
	}

	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.REMOTE_WEB_DRIVER;
	}

	public String getRemoteServerUrl() {
		return remoteServerUrl;
	}

	public void setRemoteServerUrl(String remoteServerUrl) {
		this.remoteServerUrl = remoteServerUrl;
	}
	
	@Override
	public Map<String, String> getPropertyMap() {
		Map<String, String> propertyMap = super.getPropertyMap();
		propertyMap.put(DriverFactory.REMOTE_WEB_DRIVER_URL, remoteServerUrl);
		return propertyMap;
	}

}
