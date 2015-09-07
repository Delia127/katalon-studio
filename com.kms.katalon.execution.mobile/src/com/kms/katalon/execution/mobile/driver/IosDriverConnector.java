package com.kms.katalon.execution.mobile.driver;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class IosDriverConnector extends AbstractMobileDriverConnector {

	public IosDriverConnector(String deviceName) {
		super(deviceName);
	}

	@Override
	public DriverType getDriverType() {
		return MobileDriverType.IOS_DRIVER;
	}
}
