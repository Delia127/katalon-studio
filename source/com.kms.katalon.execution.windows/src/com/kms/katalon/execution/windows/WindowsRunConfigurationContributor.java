package com.kms.katalon.execution.windows;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.windows.driver.WindowsDriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;

public class WindowsRunConfigurationContributor implements IRunConfigurationContributor {

    public WindowsRunConfigurationContributor() {
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Collections.emptyList();
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
    }

    @Override
    public String getId() {
        return WindowsDriverType.getInstance().getName();
    }

    @Override
    public int getPreferredOrder() {
        return 8;
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new WindowsRunConfiguration(new WindowsDriverConnector(
                projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptions(RunConfigurationDescription description) {
        return Collections.emptyList();
    }

}
