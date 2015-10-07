package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class IosDriverConnector extends AbstractMobileDriverConnector {

    public IosDriverConnector(String projectDir) throws IOException {
        super(projectDir);
    }

    public IosDriverConnector(String projectDir, String customProfileName)
            throws IOException {
        super(projectDir, customProfileName); 
    }

	@Override
	public DriverType getDriverType() {
		return MobileDriverType.IOS_DRIVER;
	}
}
