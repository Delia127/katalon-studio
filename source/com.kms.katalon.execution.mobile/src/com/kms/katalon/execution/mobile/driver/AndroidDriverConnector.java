package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class AndroidDriverConnector extends MobileDriverConnector {

    public AndroidDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }

    @Override
    public DriverType getDriverType() {
        return MobileDriverType.ANDROID_DRIVER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            AndroidDriverConnector androidDriverConnector = new AndroidDriverConnector(getParentFolderPath());
            androidDriverConnector.setDeviceName(getDeviceName());
            androidDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getDriverProperties());
            return androidDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }

}
