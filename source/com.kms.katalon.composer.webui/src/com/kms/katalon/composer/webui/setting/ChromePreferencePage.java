package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;

public class ChromePreferencePage extends DriverPreferencePage {

    @Override
    protected IDriverConnector getDriverConnector(String configurationFolderPath) {
        try {
            return new ChromeDriverConnector(configurationFolderPath);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            // IO Errors, return null
            return null;
        }
    }

}
