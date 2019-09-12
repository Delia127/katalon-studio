package com.kms.katalon.execution.windows;

import java.io.IOException;

import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;

public class WindowsDriverConnectorContributor implements IDriverConnectorContributor {

    public WindowsDriverConnectorContributor() {
    }

    @Override
    public String getName() {
        return "Windows";
    }

    @Override
    public IDriverConnector[] getDriverConnector(String configFolderPath) throws IOException {
        return new IDriverConnector[] { new WindowsDriverConnector(configFolderPath)};
    }

}
