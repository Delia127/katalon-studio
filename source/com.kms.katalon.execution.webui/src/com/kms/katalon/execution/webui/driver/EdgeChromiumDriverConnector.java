package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class EdgeChromiumDriverConnector extends WebUiDriverConnector {
    
    private String driverPath;

    public EdgeChromiumDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        
    }
    @Override
    public DriverType getDriverType() {
        return WebUIDriverType.EDGE_CHROMIUM_DRIVER;
    }
    
    @Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> propertyMap = super.getSystemProperties();
        propertyMap.put(StringConstants.CONF_PROPERTY_EDGE_CHROMIUM_DRIVER_PATH, driverPath);
        return propertyMap;
    }
    
    public String getEdgeDriverPath() {
        return driverPath;
    }

    public void setEdgeDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            EdgeChromiumDriverConnector edgeChromiumDriverConnector  = new EdgeChromiumDriverConnector(getParentFolderPath());
            edgeChromiumDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
            return edgeChromiumDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }

}
