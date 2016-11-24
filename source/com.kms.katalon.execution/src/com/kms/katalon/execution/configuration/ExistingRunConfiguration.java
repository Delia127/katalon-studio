package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.logging.LogUtil;

public class ExistingRunConfiguration extends AbstractRunConfiguration {
    protected String sessionId;

    protected String remoteUrl;

    protected String driverName;

    protected String projectDir;

    public ExistingRunConfiguration(String projectDir) {
        this.projectDir = projectDir;
    }

    public ExistingRunConfiguration(String projectDir, String sessionId, String remoteUrl, String driverName) {
        this.projectDir = projectDir;
        this.sessionId = sessionId;
        this.remoteUrl = remoteUrl;
        this.driverName = driverName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> webUIDriverCollector = new LinkedHashMap<String, IDriverConnector>();
        try {
            webUIDriverCollector.put(DriverFactory.EXISTING_DRIVER_PROPERTY,
                    new ExistingDriverConnector(
                            projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME,
                            sessionId, remoteUrl, driverName));
        } catch (IOException e) {
            LogUtil.logError(e);
        }
        return webUIDriverCollector;
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new ExistingRunConfiguration(projectDir, sessionId, remoteUrl, driverName);
    }

}
