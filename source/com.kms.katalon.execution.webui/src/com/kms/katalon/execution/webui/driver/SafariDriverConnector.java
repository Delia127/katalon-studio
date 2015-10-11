package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class SafariDriverConnector extends WebUiDriverConnector {
    
    public SafariDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.SAFARI_DRIVER;
	}
}
