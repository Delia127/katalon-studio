package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.impl.RemoteExecutionSetting;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebRunConfiguration extends AbstractRunConfiguration {
    
    protected RemoteWebDriverConnector remoteDriverConnector;

    protected String projectDir;

    public RemoteWebRunConfiguration(String projectDir, RemoteWebDriverConnector webUiDriverConnector)
            throws IOException {
        this.projectDir = projectDir;
        this.remoteDriverConnector = webUiDriverConnector;
    }

    public RemoteWebDriverConnector getRemoteDriverConnector() {
        return remoteDriverConnector;
    }

    public RemoteWebRunConfiguration(String projectDir) throws IOException {
        this(projectDir, new RemoteWebDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }
    
    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> driverCollector = new LinkedHashMap<String, IDriverConnector>();
        driverCollector.put(RunConfiguration.REMOTE_DRIVER_PROPERTY, remoteDriverConnector);
        return driverCollector;
    }
    
    @Override
    protected void initExecutionSetting() {
        executionSetting = new RemoteExecutionSetting();
    }
    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new RemoteWebRunConfiguration(projectDir);
    }

    @Override
    public String getName() {
        String remoteServerUrl = ((RemoteWebDriverConnector) remoteDriverConnector).getRemoteServerUrl();
        return super.getName() + " - " + StringUtils.defaultIfEmpty(remoteServerUrl, "<empty server URL>");
    }

    public String getRemoteServerUrl() {
        return ((RemoteWebDriverConnector) remoteDriverConnector).getRemoteServerUrl();
    }

    public void setRemoteServerUrl(String remoteServerUrl) {
        ((RemoteWebDriverConnector) remoteDriverConnector).setRemoteServerUrl(remoteServerUrl);
    }

    public RemoteWebDriverConnectorType getRemoteWebDriverConnectorType() {
        return ((RemoteWebDriverConnector) remoteDriverConnector).getRemoteWebDriverConnectorType();
    }

    public void setRemoteWebDriverConnectorType(RemoteWebDriverConnectorType remoteWebDriverConnectorType) {
        ((RemoteWebDriverConnector) remoteDriverConnector).setRemoteWebDriverConnectorType(remoteWebDriverConnectorType);
    }
}
