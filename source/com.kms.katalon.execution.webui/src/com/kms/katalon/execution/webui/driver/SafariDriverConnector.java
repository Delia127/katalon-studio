package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class SafariDriverConnector extends WebUiDriverConnector {
    
    public SafariDriverConnector(String projectDir) throws IOException {
        super(projectDir);
    }

    public SafariDriverConnector(String projectDir, String customProfileName) throws IOException {
        super(projectDir, customProfileName);
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.SAFARI_DRIVER;
	}
}
