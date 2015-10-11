package com.kms.katalon.execution.mobile.driver.contributor;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public String getConfigFileName() throws IOException {
        return new AndroidDriverConnector("").getSettingFileName();
    }

    @Override
    public IDriverConnector getDriverConnector(String configFolderPath) throws IOException {
        return new AndroidDriverConnector(configFolderPath);
    }

    @Override
    public String getName() {
        return MobileDriverType.ANDROID_DRIVER.toString();
    }

}
