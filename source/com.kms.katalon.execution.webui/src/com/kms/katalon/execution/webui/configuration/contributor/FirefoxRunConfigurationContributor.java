package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.FirefoxRunConfiguration;

public class FirefoxRunConfigurationContributor implements IRunConfigurationContributor {

    @Override
    public String getId() {
        return WebUIDriverType.FIREFOX_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir, Map<String, String> runInput)
            throws IOException, ExecutionException {
        return new FirefoxRunConfiguration(projectDir);
    }
    
    @Override
    public int getPreferredOrder() {
        return 1;
    }

}
