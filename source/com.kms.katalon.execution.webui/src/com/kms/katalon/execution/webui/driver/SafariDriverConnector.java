package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class SafariDriverConnector extends WebUiDriverConnector {
    
    public SafariDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.SAFARI_DRIVER;
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            SafariDriverConnector safariDriverConnector = new SafariDriverConnector(getParentFolderPath());
            safariDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getDriverProperties());
            return safariDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
