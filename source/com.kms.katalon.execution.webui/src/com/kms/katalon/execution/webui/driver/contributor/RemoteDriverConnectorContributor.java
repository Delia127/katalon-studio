package com.kms.katalon.execution.webui.driver.contributor;

import java.io.IOException;

import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public String getName() {
        return "Remote";
    }

    @Override
    public IDriverConnector[] getDriverConnector(String configFolderPath) throws IOException {
        return new IDriverConnector[] { new RemoteWebDriverConnector(configFolderPath) };
    }

}
