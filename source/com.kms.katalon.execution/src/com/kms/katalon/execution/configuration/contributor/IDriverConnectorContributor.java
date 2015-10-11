package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.execution.configuration.IDriverConnector;

public interface IDriverConnectorContributor {
    public String getConfigFileName() throws IOException;

    public IDriverConnector getDriverConnector(String configFolderPath) throws IOException;
    
    public String getName();
}
