package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.webui.util.WebDriverPropertyUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;

public class ChromeRunConfiguration extends WebUiRunConfiguration {

    public ChromeRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new ChromeDriverConnector(
                projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new ChromeRunConfiguration(projectDir);
    }

    @Override
    public boolean allowsRecording() {
        return true;
    }
    
    @Override
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException, ExecutionException {
        Map<String, String> environmentVariables = new HashMap<>(super.getAdditionalEnvironmentVariables());
        environmentVariables.put(WebDriverPropertyUtil.CHROME_NO_SANDBOX, 
                String.valueOf(WebDriverPropertyUtil.isRunningInDocker()));
        return environmentVariables;
    }
}
