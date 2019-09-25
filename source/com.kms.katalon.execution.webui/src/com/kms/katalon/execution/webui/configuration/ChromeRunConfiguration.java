package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.core.webui.util.WebDriverCleanerUtil;
import com.kms.katalon.core.webui.util.WebDriverPropertyUtil;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.preferences.WebUIConsoleOptionContributor;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ChromeRunConfiguration extends WebUiRunConfiguration {

    private String driverPath;

    public ChromeRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new ChromeDriverConnector(
                projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
        this.driverPath = buildChromeDriverPath();
    }

    private String buildChromeDriverPath() {
        String driverPath = SeleniumWebDriverProvider.getChromeDriverPath();
        ScopedPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(IdConstants.KATALON_WEB_UI_BUNDLE_ID);
        boolean isUpdateDriverAllowed = store.getBoolean(WebUIConsoleOptionContributor.WEB_UI_AUTO_UPDATE_DRIVERS);
        if (isUpdateDriverAllowed && ApplicationRunningMode.get() == RunningMode.CONSOLE) {
            WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
            try {
            	//Terminate running WebDriver
            	WebDriverCleanerUtil.cleanup();
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.CHROME_DRIVER,
                        SeleniumWebDriverProvider.getTempDriverDirectory());
                String tempDriverPath = SeleniumWebDriverProvider.getTempChromeDriverPath();
                if (new File(tempDriverPath).exists()) {
                    driverPath = tempDriverPath;
                }
            } catch (InterruptedException | IOException e) {
                LogUtil.logError(e);
            }
            LogUtil.printOutputLine("Chrome driver is located at: " + driverPath);
        } else {
            LogUtil.printOutputLine(String.format(
                    "chromedriver is located at default location: %s. In case your browser is updated to a newer version,"
                            + " please use this command to update chromdriver: --config -%s=true",
                    driverPath, WebUIConsoleOptionContributor.WEB_UI_AUTO_UPDATE_DRIVERS));
        }

        return driverPath;
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
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> driverConnectors = super.getDriverConnectors();
        for (java.util.Map.Entry<String, IDriverConnector> entry : driverConnectors.entrySet()) {
            if (entry.getValue() instanceof ChromeDriverConnector) {
                ChromeDriverConnector driverConnector = (ChromeDriverConnector) entry.getValue();
                driverConnector.setChromeDriverPath(driverPath);
            }
        }
        return driverConnectors;
    }

    @Override
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException, ExecutionException {
        Map<String, String> environmentVariables = new HashMap<>(super.getAdditionalEnvironmentVariables());
        environmentVariables.put(WebDriverPropertyUtil.KATALON_DOCKER_ENV_KEY,
                String.valueOf(WebDriverPropertyUtil.isRunningInDocker()));
        return environmentVariables;
    }
}
