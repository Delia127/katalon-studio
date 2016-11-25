package com.kms.katalon.execution.configuration;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.driver.ExistingDriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.logging.LogUtil;

public class ExistingDriverConnector extends AbstractDriverConnector {
    protected String sessionId;

    protected String serverUrl;

    protected String driverName;

    public ExistingDriverConnector(String configurationFolderPath, String sessionId, String serverUrl,
            String driverName) throws IOException {
        super(configurationFolderPath);
        this.sessionId = sessionId;
        this.serverUrl = serverUrl;
        this.driverName = driverName;
    }

    @Override
    public DriverType getDriverType() {
        return new ExistingDriverType(driverName);
    }

    @Override
    public IDriverConnector clone() {
        try {
            return new ExistingDriverConnector(parentFolderPath, sessionId, serverUrl, driverName);
        } catch (IOException e) {
            LogUtil.logError(e);
            return null;
        }
    }

    @Override
    public String getSettingFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> propertyMap = super.getSystemProperties();
        propertyMap.put(StringConstants.CONF_PROPERTY_EXISTING_SESSION_SERVER_URL, serverUrl);
        propertyMap.put(StringConstants.CONF_PROPERTY_EXISTING_SESSION_SESSION_ID, sessionId);
        propertyMap.put(StringConstants.CONF_PROPERTY_EXISTING_SESSION_DRIVER_TYPE, driverName);
        return propertyMap;
    }

}
