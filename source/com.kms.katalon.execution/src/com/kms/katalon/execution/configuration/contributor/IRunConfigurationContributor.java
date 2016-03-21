package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;
import java.util.List;

import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.ConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;

public interface IRunConfigurationContributor {
    /**
     * Get id of the run configuration contributor
     * 
     * @return id of the run configuration contributor
     */
    public String getId();

    /**
     * Get the preferred order of the run configuration for consistent with execution menu
     * 
     * @return the preferred order
     */
    public int getPreferredOrder();

    /**
     * Get the run configuration
     * 
     * @param projectDir
     *            project directory
     * @param runInput
     *            map of the input arguments
     * @return the correct run configuration
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException,
            InterruptedException;

    /**
     * Get the additional required arguments for this run configuration contributor
     * 
     * @return the additional required arguments for this run configuration contributor
     */
    public List<ConsoleOption<?>> getRequiredArguments();
}
