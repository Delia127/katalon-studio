package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.webui.driver.RemoteFirefoxDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public class RemoteFirefoxPreferencePage extends RemoteDebugPreferencePage {
	
    @Override
    public WebUiDriverConnector createDriverConnector(String configurationFolderPath) {
        try {
        	if(remoteDebugDriverConnector == null){
        		remoteDebugDriverConnector = new RemoteFirefoxDriverConnector(configurationFolderPath);
        	}
            return remoteDebugDriverConnector;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

}
