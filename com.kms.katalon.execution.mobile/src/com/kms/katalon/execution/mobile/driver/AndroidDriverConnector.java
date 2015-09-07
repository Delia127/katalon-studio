package com.kms.katalon.execution.mobile.driver;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class AndroidDriverConnector extends AbstractMobileDriverConnector {

	public AndroidDriverConnector(String deviceName) {
		super(deviceName);
	}

	@Override
	public DriverType getDriverType() {
		return MobileDriverType.ANDROID_DRIVER;
	}

}
