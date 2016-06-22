package com.kms.katalon.execution.mobile.device;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.execution.mobile.exception.AndroidSetupException;
import com.kms.katalon.execution.mobile.util.ConsoleCommandExecutor;

public class AndroidDeviceInfo extends MobileDeviceInfo {
    private static final String EMULATOR_SUFFIX = ")";

    private static final String FOR_ANDROID_VERSION = " - Android ";

    private static final String EMULATOR_PREFIX = "Emulator (";

    private static final String ANDROID_EMULATOR_PREFIX = "emulator-";

    private static final String ANDROID_HOME_ENVIRONMENT_VARIABLE_NAME = "ANDROID_HOME";

    private static final String ANDROID_SDK_FOLDER_RELATIVE_PATH = "resources" + File.separator + "tools"
            + File.separator + "android_sdk";

    private static final String ADB = "adb";

    private static final String PLATFORM_TOOLS = "platform-tools";

    private static final String RO_PRODUCT_MANUFACTURER = "ro.product.manufacturer";

    private static final String RO_PRODUCT_MODEL = "ro.product.model";

    private static final String RO_BUILD_VERSION_RELEASE = "ro.build.version.release";

    private static final String NET_BT_NAME = "net.bt.name";

    private static final String GETPROP_COMMAND = "getprop";

    private static final String SHELL = "shell";

    private static final String S_FLAG = "-s";

    private static String adbPath;

    private String deviceManufacture;

    private String deviceModel;

    private String deviceOs;

    private String deviceOSVersion;
    
    private boolean isEmulator;

    public AndroidDeviceInfo(String deviceId) throws AndroidSetupException, IOException, InterruptedException {
        super(deviceId);
        isEmulator = deviceId.startsWith(ANDROID_EMULATOR_PREFIX);
        initDeviceProperties();
    }

    protected void initDeviceProperties() throws IOException, InterruptedException, AndroidSetupException {
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

    public static File getAndroidSDKDirectory() throws IOException {
        return getResourceFolder(ANDROID_SDK_FOLDER_RELATIVE_PATH);
    }

    public static String getAndroidSDKDirectoryAsString() throws IOException {
        return getResourceFolder(ANDROID_SDK_FOLDER_RELATIVE_PATH).getAbsolutePath();
    }

    public static String getADBPath() throws IOException, AndroidSetupException {
        if (!StringUtils.isEmpty(adbPath)) {
            return adbPath;
        }
        adbPath = getAndroidSDKDirectoryAsString() + File.separator + PLATFORM_TOOLS + File.separator + ADB;
        return adbPath;
    }

    public static void makeAllAndroidSDKBinaryExecutable() throws IOException, InterruptedException,
            AndroidSetupException {
        if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
            return;
        }

        File androidSDKDirectory = getAndroidSDKDirectory();
        makeAllFileExecutable(androidSDKDirectory);
        makeFileExecutable(new File(getADBPath()));
    }

    private static void makeAllFileExecutable(File parentFolder) throws IOException, InterruptedException {
        if (!(parentFolder.exists() && parentFolder.isDirectory())) {
            return;
        }
        for (File file : parentFolder.listFiles()) {
            if (file.isDirectory()) {
                makeAllFileExecutable(file);
                continue;
            }
            if (!file.isFile()) {
                continue;
            }
            makeFileExecutable(file);
        }
    }

    @Override
    public String getDisplayName() {
        if (isEmulator) {
            return EMULATOR_PREFIX + getDeviceModel() + FOR_ANDROID_VERSION + getDeviceOSVersion() + EMULATOR_SUFFIX;
        }
        return getDeviceManufacturer() + " " + getDeviceModel() + " " + getDeviceOSVersion();
    }
    
    @Override
    public String getDeviceName() {
        return getDisplayName();
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

    public static Map<String, String> getAndroidAdditionalEnvironmentVariables() throws IOException {
        String androidSDKFolder = getAndroidSDKDirectoryAsString();
        if (StringUtils.isEmpty(androidSDKFolder)) {
            return new HashMap<String, String>();
        }
        Map<String, String> addtionalEnvironmentVariables = new HashMap<String, String>();
        addtionalEnvironmentVariables.put(ANDROID_HOME_ENVIRONMENT_VARIABLE_NAME, androidSDKFolder);
        return addtionalEnvironmentVariables;
    }
}
