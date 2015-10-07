package com.kms.katalon.composer.mobile.setting;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosPreferencePage extends AbstractMobilePreferencePage {
    
    @Override
    protected IDriverConnector getDriverConnector(String projectFolderLocation) {
        try {
            return new IosDriverConnector(projectFolderLocation);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            // IO Errors, return null
            return null;
        }
    }

}
