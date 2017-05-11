package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

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
        
        public static int indexOf(RemoteWebDriverConnectorType remoteType) {
            RemoteWebDriverConnectorType[] values = values();
            for (int index = 0; index < values.length; index++) {
                if (values[index] == remoteType) {
                    return index;
                }
            }
            return -1;
        }
    }

    protected String remoteServerUrl;
    protected RemoteWebDriverConnectorType remoteWebDriverConnectorType;

    /**
     * @param configurationFolderPath It should be [project folder]/settings/internal
     * 
     */
    public RemoteWebDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
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
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> propertyMap = super.getSystemProperties();
        propertyMap.put(DriverFactory.REMOTE_WEB_DRIVER_URL, getRemoteServerUrl());
        propertyMap.put(DriverFactory.REMOTE_WEB_DRIVER_TYPE, getRemoteWebDriverConnectorType().name());
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
    public void saveUserConfigProperties() throws IOException {
        driverProperties.put(DriverFactory.REMOTE_WEB_DRIVER_URL, getRemoteServerUrl());
        driverProperties.put(DriverFactory.REMOTE_WEB_DRIVER_TYPE, getRemoteWebDriverConnectorType().name());
        super.saveUserConfigProperties();
    }

    @Override
    public String toString() {
        Map<String, Object> tempMap = new HashMap<String, Object>(getUserConfigProperties());
        tempMap.put(DriverFactory.REMOTE_WEB_DRIVER_URL, getRemoteServerUrl());
        tempMap.put(DriverFactory.REMOTE_WEB_DRIVER_TYPE, getRemoteWebDriverConnectorType().name());
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
    public RemoteWebDriverConnector clone() {
        try {
            RemoteWebDriverConnector remoteDriverConnector = new RemoteWebDriverConnector(getParentFolderPath());
            remoteDriverConnector.setRemoteServerUrl(getRemoteServerUrl());
            remoteDriverConnector.setRemoteWebDriverConnectorType(getRemoteWebDriverConnectorType());
            remoteDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
            return remoteDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
