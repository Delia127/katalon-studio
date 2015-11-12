package com.kms.katalon.execution.mobile.driver.contributor;

import java.io.IOException;

import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class MobileDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public IDriverConnector[] getDriverConnector(String configFolderPath) throws IOException {
        return new IDriverConnector[] {new IosDriverConnector(configFolderPath), new AndroidDriverConnector(configFolderPath)} ;
    }

}
