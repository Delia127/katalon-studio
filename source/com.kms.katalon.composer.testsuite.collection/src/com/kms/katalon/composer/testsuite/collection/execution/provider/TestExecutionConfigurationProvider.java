package com.kms.katalon.composer.testsuite.collection.execution.provider;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public interface TestExecutionConfigurationProvider extends TestExecutionItem {
    IRunConfigurationContributor getRunConfigurationContributor();
    
    RunConfigurationDescription toConfigurationEntity();
}
