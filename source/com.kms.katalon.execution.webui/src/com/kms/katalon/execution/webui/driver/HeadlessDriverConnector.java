package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class HeadlessDriverConnector extends WebUiDriverConnector {

    private String chromeDriverPath;

    public HeadlessDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        setChromeDriverPath(SeleniumWebDriverProvider.getChromeDriverPath());
    }

    @Override
    public DriverType getDriverType() {
        return WebUIDriverType.CHROME_HEADLESS_DRIVER;
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> propertyMap = super.getSystemProperties();
        propertyMap.put(StringConstants.CONF_PROPERTY_CHROME_DRIVER_PATH, getChromeDriverPath());
        return propertyMap;
    }

    public String getChromeDriverPath() {
        return chromeDriverPath;
    }

    public void setChromeDriverPath(String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            HeadlessDriverConnector headlessDriverConnector = new HeadlessDriverConnector(getParentFolderPath());
            headlessDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(
                    getUserConfigProperties());
            return headlessDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
