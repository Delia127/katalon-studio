package com.kms.katalon.execution.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.execution.exception.ExecutionException;

public class VariableEvaluationRunConfiguration extends AbstractRunConfiguration {

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        return new HashMap<>();
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new VariableEvaluationRunConfiguration();
    }
  
}
