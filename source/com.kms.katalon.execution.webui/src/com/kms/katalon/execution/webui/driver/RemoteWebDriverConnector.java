package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteWebDriverConnector extends WebUiDriverConnector {
	private String remoteServerUrl;
	
	public RemoteWebDriverConnector(String projectDir, String remoteServerUrl) throws IOException {
        super(projectDir);
        setRemoteServerUrl(remoteServerUrl);
    }

    public RemoteWebDriverConnector(String projectDir, String customProfileName, String remoteServerUrl) throws IOException {
        super(projectDir, customProfileName);
        setRemoteServerUrl(remoteServerUrl);
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
	public Map<String, Object> getExecutionSettingPropertyMap() {
		Map<String, Object> propertyMap = super.getExecutionSettingPropertyMap();
		propertyMap.put(DriverFactory.REMOTE_WEB_DRIVER_URL, remoteServerUrl);
		return propertyMap;
	}

}
