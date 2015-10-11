package com.kms.katalon.execution.webui.driver.contributor;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;

public class ChromeDriverConnectorContributor implements IDriverConnectorContributor {

    @Override
    public String getConfigFileName() throws IOException {
        return new ChromeDriverConnector("").getSettingFileName();
    }

    @Override
    public IDriverConnector getDriverConnector(String configFolderPath) throws IOException {
        return new ChromeDriverConnector(configFolderPath);
    }
    
    @Override
    public String getName() {
        return WebUIDriverType.CHROME_DRIVER.toString();
    }
}
