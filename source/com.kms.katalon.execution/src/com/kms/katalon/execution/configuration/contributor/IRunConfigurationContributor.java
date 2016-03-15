package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;

public interface IRunConfigurationContributor {
    public String getId();
    
    // Set preferred order for consistent with execution menu
    public int getPreferredOrder();

    public IRunConfiguration getRunConfiguration(String projectDir, Map<String, String> runInput)
            throws IOException, ExecutionException, InterruptedException;
}
