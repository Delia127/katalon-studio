package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.execution.configuration.IDriverConnector;

public interface IDriverConnectorContributor {
    public IDriverConnector[] getDriverConnector(String configFolderPath) throws IOException;
}
