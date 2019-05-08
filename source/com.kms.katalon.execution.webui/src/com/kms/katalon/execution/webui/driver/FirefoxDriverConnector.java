package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class FirefoxDriverConnector extends WebUiDriverConnector {
    private String geckoDriverPath;

    public FirefoxDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        geckoDriverPath = SeleniumWebDriverProvider.getGeckoDriverPath();
    }

    @Override
    public DriverType getDriverType() {
        return WebUIDriverType.FIREFOX_DRIVER;
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> propertyMap = super.getSystemProperties();
        propertyMap.put(StringConstants.CONF_PROPERTY_GECKO_DRIVER_PATH, geckoDriverPath);
        return propertyMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            FirefoxDriverConnector firefoxDriverConnector = new FirefoxDriverConnector(getParentFolderPath());
            firefoxDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(
                    getUserConfigProperties());
            return firefoxDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }

    public String getGeckoDriverPath() {
        return geckoDriverPath;
    }

    public void setGeckoDriverPath(String geckoDriverPath) {
        this.geckoDriverPath = geckoDriverPath;
    }
}
