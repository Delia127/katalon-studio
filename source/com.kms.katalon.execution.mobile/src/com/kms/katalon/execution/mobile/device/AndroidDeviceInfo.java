package com.kms.katalon.execution.mobile.device;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.mobile.exception.AndroidSetupException;
import com.kms.katalon.execution.mobile.util.ConsoleCommandExecutor;

public class AndroidDeviceInfo extends MobileDeviceInfo {
    private static final String ADB = "adb";

    private static final String PLATFORM_TOOLS = "platform-tools";

    private static final String RO_PRODUCT_MANUFACTURER = "ro.product.manufacturer";

    private static final String RO_PRODUCT_MODEL = "ro.product.model";

    private static final String RO_BUILD_VERSION_RELEASE = "ro.build.version.release";

    private static final String NET_BT_NAME = "net.bt.name";

    private static final String GETPROP_COMMAND = "getprop";

    private static final String SHELL = "shell";

    private static final String S_FLAG = "-s";

    public static final String ANDROID_HOME_SYSTEM_ENVIRONMENT_VARIABLE_NAME = "ANDROID_HOME";

    private static String adbPath;

    private String deviceManufacture;

    private String deviceModel;

    private String deviceOs;

    private String deviceOSVersion;

    public AndroidDeviceInfo(String deviceId) throws AndroidSetupException, IOException, InterruptedException {
        super(deviceId);
        deviceManufacture = initAndroidDeviceManufacturer();
        deviceModel = initAndroidDeviceModel();
        deviceOs = initAndroidDeviceOS();
        deviceOSVersion = initAndroidDeviceOSVersion();
    }

    private String initAndroidDeviceOS() throws AndroidSetupException, IOException, InterruptedException {
        String[] getOSCommand = new String[] { getADBPath(), S_FLAG, this.deviceId, SHELL, GETPROP_COMMAND, NET_BT_NAME };
        return ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(getOSCommand);
    }

    private String initAndroidDeviceOSVersion() throws IOException, InterruptedException, AndroidSetupException {
        String[] getManufacturerCommand = new String[] { getADBPath(), S_FLAG, deviceId, SHELL, GETPROP_COMMAND,
                RO_BUILD_VERSION_RELEASE };
        return ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(getManufacturerCommand);
    }

    private String initAndroidDeviceModel() throws AndroidSetupException, IOException, InterruptedException {
        String[] getAndroidDeviceModelCommand = new String[] { getADBPath(), S_FLAG, deviceId, SHELL, GETPROP_COMMAND,
                RO_PRODUCT_MODEL };
        return ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(getAndroidDeviceModelCommand);
    }

    private String initAndroidDeviceManufacturer() throws IOException, InterruptedException, AndroidSetupException {
        String[] getManuFacturerCommand = new String[] { getADBPath(), S_FLAG, deviceId, SHELL, GETPROP_COMMAND,
                RO_PRODUCT_MANUFACTURER };
        return ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(getManuFacturerCommand);
    }

    public static String getADBPath() throws AndroidSetupException {
        if (!StringUtils.isEmpty(adbPath)) {
            return adbPath;
        }
        adbPath = System.getenv(ANDROID_HOME_SYSTEM_ENVIRONMENT_VARIABLE_NAME);
        if (adbPath == null) {
            throw new AndroidSetupException("System environment variable '"
                    + ANDROID_HOME_SYSTEM_ENVIRONMENT_VARIABLE_NAME + "' not found");
        }
        adbPath += File.separator + PLATFORM_TOOLS + File.separator + ADB;
        return adbPath;
    }

    @Override
    public String getDeviceName() {
        return getDeviceManufacturer() + " " + getDeviceModel() + " " + getDeviceOSVersion();
    }

    @Override
    public String getDeviceManufacturer() {
        return deviceManufacture;
    }

    @Override
    public String getDeviceModel() {
        return deviceModel;
    }

    @Override
    public String getDeviceOS() {
        return deviceOs;
    }

    @Override
    public String getDeviceOSVersion() {
        return deviceOSVersion;
    }
}
