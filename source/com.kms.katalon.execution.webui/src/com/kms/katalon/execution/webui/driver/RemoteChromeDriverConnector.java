package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class RemoteChromeDriverConnector extends RemoteDebugDriverConnector {
	
	private String chromeDriverPath;

	public RemoteChromeDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        if(super.debugPort == null || super.debugPort.isEmpty()){
        	super.setDebugPort(DriverFactory.DEFAULT_CHROME_DEBUG_PORT);
        }
        setChromeDriverPath(SeleniumWebDriverProvider.getChromeDriverPath());
    }
    
	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.REMOTE_CHROME_DRIVER;
	}
	
	public RemoteChromeDriverConnector createDriver() {
		try {
			return new RemoteChromeDriverConnector(getParentFolderPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> getSystemProperties() {
		Map<String, Object> propertyMap = super.getSystemProperties();
		propertyMap.put(StringConstants.CONF_PROPERTY_CHROME_DRIVER_PATH, getChromeDriverPath());
		return propertyMap;
	}

	@Override
	public IDriverConnector clone() {
		RemoteChromeDriverConnector connector = (RemoteChromeDriverConnector)super.clone();
		connector.setChromeDriverPath(getChromeDriverPath());
		return connector;
	}
}


