package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class AndroidDriverConnector extends MobileDriverConnector {

    public AndroidDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }

    @Override
    public DriverType getDriverType() {
        return MobileDriverType.ANDROID_DRIVER;
    }

}
