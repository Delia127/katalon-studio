package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.IERunConfiguration;

public class IERunConfigurationContributor extends WebUIRunConfigurationContributor {

    @Override
    public String getId() {
        return WebUIDriverType.IE_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException {
        return new IERunConfiguration(projectDir);
    }

    @Override
    public int getPreferredOrder() {
        return 2;
    }
}
