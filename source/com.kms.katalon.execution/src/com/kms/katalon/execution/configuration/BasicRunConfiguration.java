package com.kms.katalon.execution.configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.kms.katalon.execution.exception.ExecutionException;

public class BasicRunConfiguration extends AbstractRunConfiguration {

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        return Collections.emptyMap();
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new BasicRunConfiguration();
    }

}
