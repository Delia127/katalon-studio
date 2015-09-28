package com.kms.katalon.execution.mobile.util;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class MobileExecutionUtil {
	public static IDriverConnector getMobileDriverConnector(MobileDriverType mobileDriverType, String deviceName) {
		switch (mobileDriverType) {
		case ANDROID_DRIVER:
			return new AndroidDriverConnector(deviceName);
		case IOS_DRIVER:
			return new IosDriverConnector(deviceName);
		}
		return null;
	}
}
