package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class ChromeDriverConnector extends WebUiDriverConnector {
	private String chromeDriverPath;
	
	public ChromeDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        setChromeDriverPath(SeleniumWebDriverProvider.getChromeDriverPath());
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.CHROME_DRIVER;
	}

	@Override
	public Map<String, Object> getExecutionSettingPropertyMap() {
		Map<String, Object> propertyMap = super.getExecutionSettingPropertyMap();
		propertyMap.put(StringConstants.CONF_PROPERTY_CHROME_DRIVER_PATH, getChromeDriverPath());
		return propertyMap;
	}

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            ChromeDriverConnector iosDriverConnector = new ChromeDriverConnector(getParentFolderPath());
            iosDriverConnector.setChromeDriverPath(getChromeDriverPath());
            iosDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getDriverProperties());
            return iosDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }

}
