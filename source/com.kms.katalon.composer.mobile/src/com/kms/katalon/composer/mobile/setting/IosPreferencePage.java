package com.kms.katalon.composer.mobile.setting;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosPreferencePage extends AbstractMobilePreferencePage {
    
    @Override
    protected IDriverConnector getDriverConnector(String configurationFolderPath) {
        try {
            return new IosDriverConnector(configurationFolderPath);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            // IO Errors, return null
            return null;
        }
    }

}
