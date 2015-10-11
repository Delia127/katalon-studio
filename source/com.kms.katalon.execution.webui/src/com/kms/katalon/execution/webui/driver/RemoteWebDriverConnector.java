package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteWebDriverConnector extends WebUiDriverConnector {
    private String remoteServerUrl;

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
        return propertyMap;
    }

    @Override
    protected void loadDriverProperties() throws IOException {
        super.loadDriverProperties();
        remoteServerUrl = (driverProperties.get(DriverFactory.REMOTE_WEB_DRIVER_URL) instanceof String) ? (String) driverProperties
                .get(DriverFactory.REMOTE_WEB_DRIVER_URL) : "";
        driverProperties.remove(DriverFactory.REMOTE_WEB_DRIVER_URL);
    }

    @Override
    public void saveDriverProperties() throws IOException {
        driverProperties.put(DriverFactory.REMOTE_WEB_DRIVER_URL, remoteServerUrl);
        super.saveDriverProperties();
    }
    
    @Override
    public String toString() {
        Map<String, Object> tempMap = new HashMap<String, Object>(getDriverProperties());
        tempMap.put(DriverFactory.REMOTE_WEB_DRIVER_URL, remoteServerUrl);
        return tempMap.toString();
    }
}
