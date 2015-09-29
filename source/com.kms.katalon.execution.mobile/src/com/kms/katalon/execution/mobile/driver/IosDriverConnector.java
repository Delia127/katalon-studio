package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class IosDriverConnector extends AbstractMobileDriverConnector {

    public IosDriverConnector(String projectDir, String deviceName) throws IOException {
        super(projectDir, deviceName);
    }

    public IosDriverConnector(String projectDir, String customProfileName, String deviceName)
            throws IOException {
        super(projectDir, customProfileName, deviceName); 
    }

	@Override
	public DriverType getDriverType() {
		return MobileDriverType.IOS_DRIVER;
	}
}
