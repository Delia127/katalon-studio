package com.kms.katalon.execution.webservice.configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.BasicRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webservice.configuration.impl.WebServiceExecutionSetting;

public class WebServiceRunConfiguration extends AbstractRunConfiguration {

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        return Collections.emptyMap();
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new WebServiceRunConfiguration();
    }

    @Override
    protected void initExecutionSetting() {
        executionSetting = new WebServiceExecutionSetting();
    }
}
