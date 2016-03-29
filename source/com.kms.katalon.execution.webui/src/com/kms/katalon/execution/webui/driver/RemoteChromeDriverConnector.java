package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteChromeDriverConnector extends RemoteDebugDriverConnector {
	
	public RemoteChromeDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.REMOTE_CHROME_DRIVER;
	}
	
	@Override
	public RemoteChromeDriverConnector createDriver() {
		try {
			return new RemoteChromeDriverConnector(getParentFolderPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
