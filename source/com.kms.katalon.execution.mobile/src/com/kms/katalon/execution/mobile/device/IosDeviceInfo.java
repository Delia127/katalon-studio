package com.kms.katalon.execution.mobile.device;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.util.ConsoleCommandExecutor;

public class IosDeviceInfo extends MobileDeviceInfo {

    private static final String RELATIVE_PATH_TO_TOOLS_FOLDER = "resources" + File.separator + "tools" + File.separator;

    private static final String PATH = "PATH";

    private static final String DYLD_LIBRARY_PATH = "DYLD_LIBRARY_PATH";

    private static final String PRODUCT_TYPE_INFO_PREFIX = "ProductType:";

    private static final String PRODUCT_VERSION_INFO_PREFIX = "ProductVersion:";

    private static final String DEVICE_NAME_INFO_PREFIX = "DeviceName:";

    private static final String DEVICE_CLASS_INFO_PREFIX = "DeviceClass:";

    private static final String IMOBILE_DEVICE_FOLDER_RELATIVE_PATH = RELATIVE_PATH_TO_TOOLS_FOLDER + "imobiledevice";

    private static final String IOS_DEPLOY_FOLDER_RELATIVE_PATH = RELATIVE_PATH_TO_TOOLS_FOLDER + "ios-deploy";

    private static final String CARTHAGE_FOLDER_RELATIVE_PATH = RELATIVE_PATH_TO_TOOLS_FOLDER + "carthage"
            + File.separator + "0.18.1" + File.separator + "bin";
    
    public static final String DEVICECONSOLE = "deviceconsole";
    
    private static final String DEVICE_CONSOLE_FOLDER_RELATIVE_PATH = RELATIVE_PATH_TO_TOOLS_FOLDER + DEVICECONSOLE;
    
    protected String deviceClass = "";

    protected String deviceName = "";

    protected String deviceOSVersion = "";

    protected String deviceType = "";

    public IosDeviceInfo(String deviceId) throws IOException, InterruptedException {
        super(deviceId);
        initDeviceInfos(deviceId);
    }

    protected void initDeviceInfos(String deviceId) throws IOException, InterruptedException {
        String[] deviceInfoCommand = new String[] {
                getIMobileDeviceDirectoryAsString() + File.separator + "ideviceinfo", "-u", deviceId };
        List<String> deviceInfos = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(deviceInfoCommand,
                getIosAdditionalEnvironmentVariables());
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
                deviceOSVersion = deviceInfo.substring(PRODUCT_VERSION_INFO_PREFIX.length(), deviceInfo.length())
                        .trim();
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
        return deviceName;
    }

    @Override
    public String getDisplayName() {
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
        return getResourceFolder(IMOBILE_DEVICE_FOLDER_RELATIVE_PATH);
    }

    private static File getIosDeployDirectory() throws IOException {
        return getResourceFolder(IOS_DEPLOY_FOLDER_RELATIVE_PATH);
    }

    private static File getCarthageDirectory() throws IOException {
        return getResourceFolder(CARTHAGE_FOLDER_RELATIVE_PATH);
    }
    
    public static File getDeviceConsoleExecutablePath() throws IOException {
        return new File(getResourceFolder(DEVICE_CONSOLE_FOLDER_RELATIVE_PATH), DEVICECONSOLE);
    }

    public static String getIMobileDeviceDirectoryAsString() throws IOException {
        return getIMobileDeviceDirectory().getAbsolutePath();
    }

    public static Map<String, String> getIosAdditionalEnvironmentVariables() throws IOException {
        Map<String, String> additionalEnvironmentVariables = new HashMap<String, String>();
        String iMobileDeviceDirectory = getIMobileDeviceDirectoryAsString();
        if (StringUtils.isEmpty(iMobileDeviceDirectory)) {
            return new HashMap<String, String>();
        }
        additionalEnvironmentVariables.put(DYLD_LIBRARY_PATH, System.getenv(PATH) + ":" + iMobileDeviceDirectory);
        additionalEnvironmentVariables.put(PATH, System.getenv(PATH) + ":" + iMobileDeviceDirectory + ":"
                + getIosDeployDirectory().getAbsolutePath() + ":" + getCarthageDirectory().getAbsolutePath());
        return additionalEnvironmentVariables;
    }

    public static void makeAllIMobileDeviceBinaryExecuteAble() throws IOException, InterruptedException {
        if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
            return;
        }
        File iMobileDeviceBinDirectory = getIMobileDeviceDirectory();
        if (!(iMobileDeviceBinDirectory.exists() && iMobileDeviceBinDirectory.isDirectory())) {
            return;
        }
        for (File file : iMobileDeviceBinDirectory.listFiles()) {
            if (!file.isFile()) {
                continue;
            }
            makeFileExecutable(file);
        }
    }
}
