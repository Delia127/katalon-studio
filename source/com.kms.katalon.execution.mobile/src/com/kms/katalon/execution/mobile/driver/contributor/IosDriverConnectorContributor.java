package com.kms.katalon.execution.mobile.driver.contributor;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public String getConfigFileName() throws IOException {
        return new IosDriverConnector("").getSettingFileName();
    }

    @Override
    public IDriverConnector getDriverConnector(String configFolderPath) throws IOException {
        return new IosDriverConnector(configFolderPath);
    }
    
    @Override
    public String getName() {
        return MobileDriverType.IOS_DRIVER.toString();
    }
}
