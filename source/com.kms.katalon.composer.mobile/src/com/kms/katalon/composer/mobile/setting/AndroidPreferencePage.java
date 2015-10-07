package com.kms.katalon.composer.mobile.setting;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidPreferencePage extends AbstractMobilePreferencePage {

    @Override
    protected IDriverConnector getDriverConnector(String projectFolderLocation) {
        try {
            return new AndroidDriverConnector(projectFolderLocation);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            // IO Errors, return null
            return null;
        }
    }

}
