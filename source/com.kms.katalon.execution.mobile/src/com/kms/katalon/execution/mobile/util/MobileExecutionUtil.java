package com.kms.katalon.execution.mobile.util;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class MobileExecutionUtil {
    public static IDriverConnector getMobileDriverConnector(MobileDriverType mobileDriverType, String projectDirectory,
            String deviceName) throws IOException {
        switch (mobileDriverType) {
        case ANDROID_DRIVER:
            return new AndroidDriverConnector(projectDirectory, deviceName);
        case IOS_DRIVER:
            return new IosDriverConnector(projectDirectory, deviceName);
        }
        return null;
    }
}
