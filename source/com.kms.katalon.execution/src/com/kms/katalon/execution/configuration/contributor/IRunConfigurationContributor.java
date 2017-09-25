package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;
import java.util.List;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public interface IRunConfigurationContributor extends ConsoleOptionContributor {
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
     * Find the correct run configuration
     * 
     * @param projectDir project directory
     * @return the correct run configuration
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException,
            InterruptedException;

    /**
     * Find the correct run configuration
     * 
     * @param projectDir project directory
     * @param runConfigurationDescription run configuration description
     * @return the correct run configuration
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public IRunConfiguration getRunConfiguration(String projectDir,
            RunConfigurationDescription runConfigurationDescription) throws IOException, ExecutionException,
            InterruptedException;

    public List<ConsoleOption<?>> getConsoleOptions(RunConfigurationDescription description);
}
