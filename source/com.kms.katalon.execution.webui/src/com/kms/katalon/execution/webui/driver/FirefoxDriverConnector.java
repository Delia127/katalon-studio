package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class FirefoxDriverConnector extends WebUiDriverConnector {
    public FirefoxDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }

	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.FIREFOX_DRIVER;
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            FirefoxDriverConnector firefoxDriverConnector = new FirefoxDriverConnector(getParentFolderPath());
            firefoxDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getDriverProperties());
            return firefoxDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
