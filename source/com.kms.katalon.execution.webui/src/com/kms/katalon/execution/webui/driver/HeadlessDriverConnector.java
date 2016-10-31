package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class HeadlessDriverConnector extends WebUiDriverConnector {
    
    public HeadlessDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
    @Override
    public DriverType getDriverType() {
        return WebUIDriverType.HEADLESS_DRIVER;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            HeadlessDriverConnector headlessDriverConnector = new HeadlessDriverConnector(getParentFolderPath());
            headlessDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
            return headlessDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}