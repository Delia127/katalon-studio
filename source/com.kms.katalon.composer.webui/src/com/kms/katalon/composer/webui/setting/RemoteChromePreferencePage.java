package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;

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
	
	public void doInstallDebuger(){	
		MessageDialog.openInformation(getShell(), "Configuration", 
				"To enable Chrome remote accessibilty, you need to start Chrome in Developer mode "
				+ "by adding these command args into your Chrome executable: \n\n"
				+ "chrome.exe --remote-debugging-port=" 
				+ ((RemoteChromeDriverConnector)remoteDebugDriverConnector).getDebugPort());
		
	}
}
