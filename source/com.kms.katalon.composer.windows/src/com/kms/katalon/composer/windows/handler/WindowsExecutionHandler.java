package com.kms.katalon.composer.windows.handler;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.windows.WindowsDriverConnector;
import com.kms.katalon.execution.windows.WindowsRunConfiguration;

public class WindowsExecutionHandler extends AbstractExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException {
        return new WindowsRunConfiguration(new WindowsDriverConnector(
                projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }
}
