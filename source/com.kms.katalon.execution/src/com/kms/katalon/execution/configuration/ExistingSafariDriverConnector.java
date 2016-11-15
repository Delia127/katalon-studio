package com.kms.katalon.execution.configuration;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.logging.LogUtil;

public class ExistingSafariDriverConnector extends ExistingDriverConnector {
    private String port;

    public ExistingSafariDriverConnector(String configurationFolderPath, String port, String sessionId,
            String serverUrl, String driverName) throws IOException {
        super(configurationFolderPath, sessionId, serverUrl, driverName);
        this.port = port;
    }

    @Override
    public IDriverConnector clone() {
        try {
            return new ExistingSafariDriverConnector(parentFolderPath, port, sessionId, serverUrl, driverName);
        } catch (IOException e) {
            LogUtil.logError(e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> propertyMap = super.getSystemProperties();
        propertyMap.put(StringConstants.CONF_PROPERTY_EXISTING_SESSION_PORT, port);
        return propertyMap;
    }
}
