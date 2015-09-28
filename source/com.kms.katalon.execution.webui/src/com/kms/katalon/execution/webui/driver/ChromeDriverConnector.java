package com.kms.katalon.execution.webui.driver;

import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.entity.AbstractDriverConnector;

public class ChromeDriverConnector extends AbstractDriverConnector {
	private String chromeDriverPath;
	
	public ChromeDriverConnector() {
		setChromeDriverPath(SeleniumWebDriverProvider.getChromeDriverPath());
	}

	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.CHROME_DRIVER;
	}

	@Override
	public Map<String, String> getPropertyMap() {
		Map<String, String> propertyMap = super.getPropertyMap();
		propertyMap.put(StringConstants.CONF_PROPERTY_CHROME_DRIVER_PATH, getChromeDriverPath());
		return propertyMap;
	}

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

}
