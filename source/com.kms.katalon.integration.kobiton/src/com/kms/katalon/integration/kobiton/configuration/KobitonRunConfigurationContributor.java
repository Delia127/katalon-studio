package com.kms.katalon.integration.kobiton.configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.contributor.WebUIRunConfigurationContributor;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

public class KobitonRunConfigurationContributor extends WebUIRunConfigurationContributor {
    private static final int PREFERRED_ORDER = 8;

    @Override
    public String getId() {
        return WebUIDriverType.KOBITON_WEB_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException {
        KobitonRunConfiguration runConfiguration = new KobitonRunConfiguration(projectDir);
        return runConfiguration;
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir,
            RunConfigurationDescription runConfigurationDescription)
            throws IOException, ExecutionException, InterruptedException {
        KobitonRunConfiguration runConfiguration = new KobitonRunConfiguration(projectDir);

        KobitonDevice device = JsonUtil.fromJson(runConfigurationDescription.getRunConfigurationData()
                .get(KobitonRunConfiguration.KOBITON_DEVICE_PROPERTY), KobitonDevice.class);

        runConfiguration.setApiKey(KobitonPreferencesProvider.getKobitonApiKey());
        runConfiguration.setUserName(KobitonPreferencesProvider.getKobitonUserName());
        runConfiguration.setKobitonDevice(device);
        return runConfiguration;
    }

    @Override
    public int getPreferredOrder() {
        return PREFERRED_ORDER;
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Collections.emptyList();
    }
}
