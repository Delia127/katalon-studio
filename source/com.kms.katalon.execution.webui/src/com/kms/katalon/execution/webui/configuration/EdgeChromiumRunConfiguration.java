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
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.preferences.WebUIConsoleOptionContributor;
import com.kms.katalon.execution.webui.driver.EdgeChromiumDriverConnector;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class EdgeChromiumRunConfiguration extends WebUiRunConfiguration {

    private String edgeChromiumDriverPath;
    
    public EdgeChromiumRunConfiguration(String projectDir)
            throws IOException {
        super(projectDir, new EdgeChromiumDriverConnector(projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
        this.edgeChromiumDriverPath = buildEdgeDriverPath();
    }

    private String buildEdgeDriverPath() {
        String driverPath = SeleniumWebDriverProvider.getEdgeChromiumDriverPath();
        ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_WEB_UI_BUNDLE_ID);
        boolean isUpdateDriverAllowed = store.getBoolean(WebUIConsoleOptionContributor.WEB_UI_AUTO_UPDATE_DRIVERS);
        if (isUpdateDriverAllowed && ApplicationRunningMode.get() == RunningMode.CONSOLE) {
            WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
            try {
                WebDriverCleanerUtil.terminateEdgeChromiumDriver();
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.EDGE_CHROMIUM_DRIVER,
                        SeleniumWebDriverProvider.getTempDriverDirectory());
                String tempDriverPath = SeleniumWebDriverProvider.getTempEdgeChromiumDriverPath();
                if (new File(tempDriverPath).exists()) {
                    driverPath = tempDriverPath;
                }
            } catch (InterruptedException | IOException e) {
                LogUtil.logError(e);
            }
            LogUtil.printOutputLine("Edge Chromium driver is located at: " + driverPath);
        } else {
            LogUtil.printOutputLine(String.format(
                    "Edge Chromium driver is located at default location: %s. In case your browser is updated to a newer version,"
                            + " please use this command to update Edge driver: --config -%s=true",
                    driverPath, WebUIConsoleOptionContributor.WEB_UI_AUTO_UPDATE_DRIVERS));
        }
        return driverPath;
    }
    
    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> driverConnectors = super.getDriverConnectors();
        for (java.util.Map.Entry<String, IDriverConnector> entry : driverConnectors.entrySet()) {
            if (entry.getValue() instanceof EdgeChromiumDriverConnector) {
                EdgeChromiumDriverConnector driverConnector = (EdgeChromiumDriverConnector) entry.getValue();
                driverConnector.setEdgeDriverPath(edgeChromiumDriverPath);
            }
        }
        return driverConnectors;
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new EdgeChromiumRunConfiguration(projectDir);
    }

}
