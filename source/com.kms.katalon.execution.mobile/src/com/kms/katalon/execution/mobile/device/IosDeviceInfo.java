package com.kms.katalon.execution.mobile.device;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.mobile.util.ConsoleCommandExecutor;

public class IosDeviceInfo extends MobileDeviceInfo {
    private static final String PATH = "PATH";
    private static final String X_FLAG = "+x";
    private static final String CHMOD_COMMAND = "chmod";
    private static final String DYLD_LIBRARY_PATH = "DYLD_LIBRARY_PATH";
    private static final String PRODUCT_TYPE_INFO_PREFIX = "ProductType:";
    private static final String PRODUCT_VERSION_INFO_PREFIX = "ProductVersion:";
    private static final String DEVICE_NAME_INFO_PREFIX = "DeviceName:";
    private static final String DEVICE_CLASS_INFO_PREFIX = "DeviceClass:";
    private static final String IMOBILE_DEVICE_FOLDER_RELATIVE_PATH = "resources" + File.separator + "tools"
            + File.separator + "macosx" + File.separator + "imobiledevice";
    private static final String CONFIGURATION_FOLDER_NAME = "configuration";

    private String deviceClass = "";
    private String deviceName = "";
    private String deviceOSVersion = "";
    private String deviceType = "";

    public IosDeviceInfo(String deviceId) throws IOException, InterruptedException {
        super(deviceId);
        initDeviceInfos(deviceId);
    }

    private void initDeviceInfos(String deviceId) throws IOException, InterruptedException {
        String[] deviceInfoCommand = new String[] { getIMobileDeviceDirectoryAsString() + File.separator + "ideviceinfo",
                "-u", deviceId };
        List<String> deviceInfos = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(deviceInfoCommand, getIosAdditionalEnvironmentVariables());
        for (String deviceInfo : deviceInfos) {
            if (deviceInfo.contains(DEVICE_CLASS_INFO_PREFIX)) {
                deviceClass = deviceInfo.substring(DEVICE_CLASS_INFO_PREFIX.length(), deviceInfo.length()).trim();
                continue;
            }
            if (deviceInfo.contains(DEVICE_NAME_INFO_PREFIX)) {
                deviceName = deviceInfo.substring(DEVICE_NAME_INFO_PREFIX.length(), deviceInfo.length()).trim();
                continue;
            }
            if (deviceInfo.contains(PRODUCT_VERSION_INFO_PREFIX)) {
                deviceOSVersion = deviceInfo.substring(PRODUCT_VERSION_INFO_PREFIX.length(), deviceInfo.length()).trim();
                continue;
            }
            if (deviceInfo.contains(PRODUCT_TYPE_INFO_PREFIX)) {
                deviceType = deviceInfo.substring(PRODUCT_TYPE_INFO_PREFIX.length(), deviceInfo.length()).trim();
                continue;
            }
        }
    }

    @Override
    public String getDeviceName() {
        return deviceClass + " " + deviceName + " " + deviceOSVersion;
    }

    @Override
    public String getDeviceManufacturer() {
        return StringConstants.KW_MANUFACTURER_APPLE;
    }

    @Override
    public String getDeviceModel() {
        return deviceType;
    }

    @Override
    public String getDeviceOS() {
        return StringConstants.KW_OS_IOS;
    }

    @Override
    public String getDeviceOSVersion() {
        return deviceOSVersion;
    }

    public static File getIMobileDeviceDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_MOBILE_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + IMOBILE_DEVICE_FOLDER_RELATIVE_PATH);
        }
        // run as product
        File configDir = ClassPathResolver.getConfigurationFolder();
        return new File(configDir.getParentFile().getAbsolutePath() + File.separator + CONFIGURATION_FOLDER_NAME
                + File.separator + IMOBILE_DEVICE_FOLDER_RELATIVE_PATH);
    }

    public static String getIMobileDeviceDirectoryAsString() throws IOException {
        return getIMobileDeviceDirectory().getAbsolutePath();
    }

    public static Map<String, String> getIosAdditionalEnvironmentVariables() throws IOException {
        Map<String, String> additionalEnvironmentVariables = new HashMap<String, String>();
        String iMobileDeviceDirectory = getIMobileDeviceDirectoryAsString();
        additionalEnvironmentVariables.put(DYLD_LIBRARY_PATH, System.getenv(PATH) + ":" + iMobileDeviceDirectory);
        additionalEnvironmentVariables.put(PATH, System.getenv(PATH) + ":" + iMobileDeviceDirectory);
        return additionalEnvironmentVariables;
    }
    
    public static void makeAllIMobileDeviceBinaryExecuteAble() throws IOException, InterruptedException {
        File iMobileDeviceBinDirectory = getIMobileDeviceDirectory();
        if (!(iMobileDeviceBinDirectory.exists() && iMobileDeviceBinDirectory.isDirectory())) {
            return;
        }
        for (File file : iMobileDeviceBinDirectory.listFiles()) {
            if (!file.isFile()) {
                continue;
            }
            ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(new String[] { CHMOD_COMMAND, X_FLAG, file.getAbsolutePath() });
        }
    }
}
