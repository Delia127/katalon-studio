package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteFirefoxDriverConnector extends RemoteDebugDriverConnector {
	
	public RemoteFirefoxDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        if(super.debugPort == null || super.debugPort.isEmpty()){
        	super.setDebugPort(DriverFactory.DEFAULT_FIREFOX_DEBUG_PORT);	
        }
    }
    
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.REMOTE_FIREFOX_DRIVER;
	}

	public RemoteDebugDriverConnector createDriver() {
		try {
			return new RemoteFirefoxDriverConnector(getParentFolderPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
