package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.device.AndroidDeviceInfo;

public class AndroidDriverConnector extends MobileDriverConnector {

    public AndroidDriverConnector(String configurationFolderPath, AndroidDeviceInfo device) throws IOException {
        super(configurationFolderPath, device);
    }
    
    public AndroidDriverConnector(String configurationFolderPath) throws IOException {
        this(configurationFolderPath, null);
    }

    @Override
    public DriverType getDriverType() {
        return MobileDriverType.ANDROID_DRIVER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            AndroidDriverConnector androidDriverConnector = new AndroidDriverConnector(getParentFolderPath(),
                    (AndroidDeviceInfo) getDevice());
            androidDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
            return androidDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }

}
