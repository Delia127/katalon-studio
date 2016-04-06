package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebRunConfiguration extends WebUiRunConfiguration {
    public RemoteWebRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new RemoteWebDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }
    
    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new RemoteWebRunConfiguration(projectDir);
    }
    
    @Override
    public String getName() {
        return super.getName() + " - " + ((RemoteWebDriverConnector) webUiDriverConnector).getRemoteServerUrl();
    }
    
    public String getRemoteServerUrl() {
        return ((RemoteWebDriverConnector) webUiDriverConnector).getRemoteServerUrl();
    }

    public void setRemoteServerUrl(String remoteServerUrl) {
        ((RemoteWebDriverConnector) webUiDriverConnector).setRemoteServerUrl(remoteServerUrl);
    }
    
    public RemoteWebDriverConnectorType getRemoteWebDriverConnectorType() {
        return ((RemoteWebDriverConnector) webUiDriverConnector).getRemoteWebDriverConnectorType();
    }

    public void setRemoteWebDriverConnectorType(RemoteWebDriverConnectorType remoteWebDriverConnectorType) {
        ((RemoteWebDriverConnector) webUiDriverConnector).setRemoteWebDriverConnectorType(remoteWebDriverConnectorType);
    }
}
