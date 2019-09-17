package com.kms.katalon.execution.windows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;

public class WindowsRunConfiguration extends AbstractRunConfiguration {

    private WindowsDriverConnector driverConnector;

    public WindowsRunConfiguration(WindowsDriverConnector driverConnector) {
        super();
        this.driverConnector = driverConnector;
    }

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> driverConnectors = new HashMap<>();
        driverConnectors.put("Windows", driverConnector);
        return driverConnectors;
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new WindowsRunConfiguration(driverConnector.clone());
    }

}
