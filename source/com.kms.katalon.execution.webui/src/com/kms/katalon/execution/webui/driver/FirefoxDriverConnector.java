package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class FirefoxDriverConnector extends WebUiDriverConnector {
    public FirefoxDriverConnector(String projectDir) throws IOException {
        super(projectDir);
    }

    public FirefoxDriverConnector(String projectDir, String customProfileName) throws IOException {
        super(projectDir, customProfileName);
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.FIREFOX_DRIVER;
	}
}
