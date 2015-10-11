package com.kms.katalon.execution.webui.driver.contributor;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;

public class IEDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public String getConfigFileName() throws IOException {
        return new IEDriverConnector("").getSettingFileName();
    }

    @Override
    public IDriverConnector getDriverConnector(String configFolderPath) throws IOException {
        return new IEDriverConnector(configFolderPath);
    }
    
    @Override
    public String getName() {
        return WebUIDriverType.IE_DRIVER.toString();
    }
}
