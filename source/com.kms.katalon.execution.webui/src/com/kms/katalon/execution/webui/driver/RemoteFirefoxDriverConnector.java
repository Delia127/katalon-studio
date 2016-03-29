package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteFirefoxDriverConnector extends RemoteDebugDriverConnector {
	
	public RemoteFirefoxDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.REMOTE_FIREFOX_DRIVER;
	}

	@Override
	public RemoteDebugDriverConnector createDriver() {
		try {
			return new RemoteFirefoxDriverConnector(getParentFolderPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
