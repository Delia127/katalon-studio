package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class RemoteWebDriverConnector extends WebUiDriverConnector {
    public enum RemoteWebDriverConnectorType {
        Selenium, Appium;
        
        public static String[] stringValues() {
            RemoteWebDriverConnectorType[] values = values();
            String[] stringValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                stringValues[i] = values[i].name();
            }
            return stringValues;
        }
    }

    private String remoteServerUrl;
    private RemoteWebDriverConnectorType remoteWebDriverConnectorType;

    public RemoteWebDriverConnector(String projectDir) throws IOException {
        super(projectDir);
    }

    @Override
    public DriverType getDriverType() {
        return WebUIDriverType.REMOTE_WEB_DRIVER;
    }

    public String getRemoteServerUrl() {
        return remoteServerUrl;
    }

    public void setRemoteServerUrl(String remoteServerUrl) {
        this.remoteServerUrl = remoteServerUrl;
    }

    @Override
    public Map<String, Object> getExecutionSettingPropertyMap() {
        Map<String, Object> propertyMap = super.getExecutionSettingPropertyMap();
        propertyMap.put(DriverFactory.REMOTE_WEB_DRIVER_URL, remoteServerUrl);
        propertyMap.put(DriverFactory.REMOTE_WEB_DRIVER_TYPE, remoteWebDriverConnectorType.name());
        return propertyMap;
    }

    @Override
    protected void loadDriverProperties() throws IOException {
        super.loadDriverProperties();
        remoteServerUrl = (driverProperties.get(DriverFactory.REMOTE_WEB_DRIVER_URL) instanceof String) ? (String) driverProperties
                .get(DriverFactory.REMOTE_WEB_DRIVER_URL) : "";
        remoteWebDriverConnectorType = (driverProperties.get(DriverFactory.REMOTE_WEB_DRIVER_TYPE) instanceof String) ? RemoteWebDriverConnectorType
                .valueOf((String) driverProperties.get(DriverFactory.REMOTE_WEB_DRIVER_TYPE))
                : RemoteWebDriverConnectorType.Selenium;
        driverProperties.remove(DriverFactory.REMOTE_WEB_DRIVER_URL);
        driverProperties.remove(DriverFactory.REMOTE_WEB_DRIVER_TYPE);
    }

    @Override
    public void saveDriverProperties() throws IOException {
        driverProperties.put(DriverFactory.REMOTE_WEB_DRIVER_URL, remoteServerUrl);
        driverProperties.put(DriverFactory.REMOTE_WEB_DRIVER_TYPE, remoteWebDriverConnectorType.name());
        super.saveDriverProperties();
    }

    @Override
    public String toString() {
        Map<String, Object> tempMap = new HashMap<String, Object>(getDriverProperties());
        tempMap.put(DriverFactory.REMOTE_WEB_DRIVER_URL, remoteServerUrl);
        tempMap.put(DriverFactory.REMOTE_WEB_DRIVER_TYPE, remoteWebDriverConnectorType.name());
        return tempMap.toString();
    }

    public RemoteWebDriverConnectorType getRemoteWebDriverConnectorType() {
        return remoteWebDriverConnectorType;
    }

    public void setRemoteWebDriverConnectorType(RemoteWebDriverConnectorType remoteWebDriverConnectorType) {
        this.remoteWebDriverConnectorType = remoteWebDriverConnectorType;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            RemoteWebDriverConnector remoteDriverConnector = new RemoteWebDriverConnector(getParentFolderPath());
            remoteDriverConnector.setRemoteServerUrl(getRemoteServerUrl());
            remoteDriverConnector.setRemoteWebDriverConnectorType(getRemoteWebDriverConnectorType());
            remoteDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getDriverProperties());
            return remoteDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
