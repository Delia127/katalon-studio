package com.kms.katalon.execution.mobile.driver.contributor;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class MobileDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public IDriverConnector[] getDriverConnector(String configFolderPath) throws IOException {
        if (Platform.OS_MACOSX.equals(Platform.getOS())) {
            return new IDriverConnector[] { new IosDriverConnector(configFolderPath),
                    new AndroidDriverConnector(configFolderPath) };
        }
        return new IDriverConnector[] { new AndroidDriverConnector(configFolderPath) };
    }

    @Override
    public String getName() {
        return DriverFactory.MOBILE_DRIVER_PROPERTY;
    }
}
