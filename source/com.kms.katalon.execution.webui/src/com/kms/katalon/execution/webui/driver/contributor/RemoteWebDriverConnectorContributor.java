package com.kms.katalon.execution.webui.driver.contributor;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteWebDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public String getConfigFileName() throws IOException {
        return new RemoteWebDriverConnector("").getSettingFileName();
    }

    @Override
    public IDriverConnector getDriverConnector(String configFolderPath) throws IOException {
        return new RemoteWebDriverConnector(configFolderPath);
    }
    
    @Override
    public String getName() {
        return WebUIDriverType.REMOTE_WEB_DRIVER.toString();
    }
}
