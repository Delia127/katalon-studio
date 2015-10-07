package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class AndroidDriverConnector extends AbstractMobileDriverConnector {

    public AndroidDriverConnector(String projectDir) throws IOException {
        super(projectDir);
    }

    public AndroidDriverConnector(String projectDir, String customProfileName)
            throws IOException {
        super(projectDir, customProfileName);
    }

    @Override
    public DriverType getDriverType() {
        return MobileDriverType.ANDROID_DRIVER;
    }

}
