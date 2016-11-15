package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.logging.LogUtil;

public class ExistingSafariRunConfiguration extends ExistingRunConfiguration {
    private String port;

    public ExistingSafariRunConfiguration(String projectDir) {
        super(projectDir);
    }

    public ExistingSafariRunConfiguration(String port, String projectDir, String sessionId, String remoteUrl,
            String driverName) {
        super(projectDir, sessionId, remoteUrl, driverName);
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> webUIDriverCollector = new LinkedHashMap<String, IDriverConnector>();
        try {
            webUIDriverCollector.put(DriverFactory.EXISTING_DRIVER_PROPERTY,
                    new ExistingSafariDriverConnector(
                            projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME,
                            port, sessionId, remoteUrl, driverName));
        } catch (IOException e) {
            LogUtil.logError(e);
        }
        return webUIDriverCollector;
    }

    @Override
    public ExistingSafariRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new ExistingSafariRunConfiguration(projectDir, port, sessionId, remoteUrl, driverName);
    }

}
