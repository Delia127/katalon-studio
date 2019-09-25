package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.core.webui.util.WebDriverCleanerUtil;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.preferences.WebUIConsoleOptionContributor;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class IERunConfiguration extends WebUiRunConfiguration {
    private String ieDriverPath;

    public IERunConfiguration(String projectDir) throws IOException {
        super(projectDir, new IEDriverConnector(
                projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
        this.ieDriverPath = buildIEDriverPath();
    }
    
    private String buildIEDriverPath() {
        String driverPath = SeleniumWebDriverProvider.getIEDriverPath();
        ScopedPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(IdConstants.KATALON_WEB_UI_BUNDLE_ID);
        boolean isUpdateDriverAllowed = store.getBoolean(WebUIConsoleOptionContributor.WEB_UI_AUTO_UPDATE_DRIVERS);
        if (isUpdateDriverAllowed && ApplicationRunningMode.get() == RunningMode.CONSOLE) {
            WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
            try {
            	//Terminate running WebDriver
            	WebDriverCleanerUtil.cleanup();
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.IE_DRIVER,
                        SeleniumWebDriverProvider.getTempDriverDirectory());
                String tempDriverPath = SeleniumWebDriverProvider.getTempIEDriverPath();
                if (new File(tempDriverPath).exists()) {
                    driverPath = tempDriverPath;
                }
            } catch (InterruptedException | IOException e) {
                LogUtil.logError(e);
            }
            LogUtil.printOutputLine("IEDriverServer is located at: "+ driverPath);
        } else {
            LogUtil.printOutputLine(String.format(
                    "IEDriverServer is located at default location: %s. In case your browser is updated to a newer version,"
                            + " please use this command to update IEDriverServer: --config -%s=true",
                    driverPath, WebUIConsoleOptionContributor.WEB_UI_AUTO_UPDATE_DRIVERS));
        }
        return driverPath;
    }

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> driverConnectors = super.getDriverConnectors();
        for (java.util.Map.Entry<String, IDriverConnector> entry : driverConnectors.entrySet()) {
            if (entry.getValue() instanceof IEDriverConnector) {
                IEDriverConnector driverConnector = (IEDriverConnector) entry.getValue();
                driverConnector.setIeDriverPath(ieDriverPath);
            }
        }
        return driverConnectors;
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new IERunConfiguration(projectDir);
    }

    @Override
    public boolean allowsRecording() {
        return true;
    }
}
