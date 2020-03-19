package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.EdgeChromiumRunConfiguration;

public class EdgeChromiumRunConfigurationContributor extends WebUIRunConfigurationContributor {

    @Override
    public String getId() {
        return WebUIDriverType.EDGE_CHROMIUM_DRIVER.toString();
    }

    @Override
    public int getPreferredOrder() {
        return 5;
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new EdgeChromiumRunConfiguration(projectDir);
    }

}
