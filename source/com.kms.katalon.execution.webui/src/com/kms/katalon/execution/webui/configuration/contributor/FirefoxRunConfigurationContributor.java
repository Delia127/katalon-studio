package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.FirefoxRunConfiguration;

public class FirefoxRunConfigurationContributor extends WebUIRunConfigurationContributor {

    @Override
    public String getId() {
        return WebUIDriverType.FIREFOX_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException {
        return new FirefoxRunConfiguration(projectDir);
    }

    @Override
    public int getPreferredOrder() {
        return 1;
    }   
}
