package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.webui.driver.RemoteChromeDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public class RemoteChromePreferencePage extends RemoteDebugPreferencePage {
	
	@Override
	protected WebUiDriverConnector createDriverConnector(String configurationFolderPath) {
		try {
        	if(remoteDebugDriverConnector == null){
        		remoteDebugDriverConnector = new RemoteChromeDriverConnector(configurationFolderPath);
        	}
            return remoteDebugDriverConnector;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
		return null;
	}
}
