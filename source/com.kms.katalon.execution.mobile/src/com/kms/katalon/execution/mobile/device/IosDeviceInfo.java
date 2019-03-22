package com.kms.katalon.execution.mobile.device;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
            + File.separator + "0.18.1" + File.separator + "etc" + File.separator + "bash_completion.d";

    public static final String DEVICECONSOLE = "deviceconsole";

    private static final String DEVICE_CONSOLE_FOLDER_RELATIVE_PATH = RELATIVE_PATH_TO_TOOLS_FOLDER + DEVICECONSOLE;

    private static final String DYLD_FALLBACK_LIBRARY_PATH = "DYLD_FALLBACK_LIBRARY_PATH";

    protected String deviceClass = "";

    protected String deviceName = "";

    protected String deviceOSVersion = "";

    protected String deviceType = "";

    public IosDeviceInfo(String deviceId) throws IOException, InterruptedException {
        super(deviceId);
        initDeviceInfos(deviceId);
    }

    public static List<String> executeCommand(String command) throws IOException, InterruptedException {
        String iMobileDeviceDirectory = IosDeviceInfo.getIMobileDeviceDirectoryAsString();
        String deviceCommandFile = "../device.sh";
        IosDeviceInfo.makeFileExecutable(new File(iMobileDeviceDirectory, deviceCommandFile));

        Map<String, String> env = new HashMap<>();
        env.put("KATALON_DEVICE_COMMAND", command);
        return ConsoleCommandExecutor.runConsoleCommandAndCollectResults(new String[] { deviceCommandFile }, env,
                iMobileDeviceDirectory);
    }

    protected void initDeviceInfos(String deviceId) throws IOException, InterruptedException {
        executeCommand("./idevicepair pair -u " + deviceId);

        List<String> deviceInfos = executeCommand("./ideviceinfo -u " + deviceId);
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
        executeCommand("./idevicepair unpair -u " + deviceId);
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(deviceId)
                .append(deviceClass)
                .append(deviceName)
                .append(deviceOSVersion)
                .append(deviceType)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IosDeviceInfo other = (IosDeviceInfo) obj;
        return new EqualsBuilder().append(this.deviceId, other.deviceId)
                .append(this.deviceClass, other.deviceClass)
                .append(this.deviceName, other.deviceName)
                .append(this.deviceOSVersion, other.deviceOSVersion)
                .append(this.deviceType, other.deviceType)
                .isEquals();
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

    @Override
    protected Map<String, String> getEnvironmentVariables() throws IOException, InterruptedException {
        return getIosAdditionalEnvironmentVariables();
    }

    public static Map<String, String> getIosAdditionalEnvironmentVariables() throws IOException, InterruptedException {
        makeIosDeployExecutable();
        makeDeviceConsoleExecutable();
        makeAllIMobileDeviceBinaryExecutable();
        makeAllFilesInFolderExecutable(getCarthageDirectory());

        Map<String, String> additionalEnvironmentVariables = new HashMap<String, String>();
        String iMobileDeviceDirectory = getIMobileDeviceDirectoryAsString();
        if (StringUtils.isNotEmpty(iMobileDeviceDirectory)) {
            additionalEnvironmentVariables.put(DYLD_LIBRARY_PATH,
                    StringUtils.defaultString(System.getenv(DYLD_LIBRARY_PATH)) + ":"
                            + iMobileDeviceDirectory);
            additionalEnvironmentVariables.put(DYLD_FALLBACK_LIBRARY_PATH,
                    StringUtils.defaultString(System.getenv(DYLD_FALLBACK_LIBRARY_PATH)) + ":"
                            + iMobileDeviceDirectory);
            additionalEnvironmentVariables.put(PATH,
                    StringUtils.defaultString(System.getenv(PATH)) + ":" 
                            + iMobileDeviceDirectory + ":"
                            + getIosDeployDirectory().getAbsolutePath() + ":"
                            + getCarthageDirectory().getAbsolutePath());
        }
        return additionalEnvironmentVariables;
    }

    public static void makeAllIMobileDeviceBinaryExecutable() throws IOException, InterruptedException {
        if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
            return;
        }
        makeAllFilesInFolderExecutable(getIMobileDeviceDirectory());
    }

    private static void makeAllFilesInFolderExecutable(File iMobileDeviceBinDirectory)
            throws IOException, InterruptedException {
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

    public static void makeIosDeployExecutable() throws IOException, InterruptedException {
        if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
            return;
        }
        makeAllFilesInFolderExecutable(getIosDeployDirectory());
    }

    public static void makeDeviceConsoleExecutable() throws IOException, InterruptedException {
        if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
            return;
        }
        File deviceConsoleBinary = getDeviceConsoleExecutablePath();
        if (!deviceConsoleBinary.isFile()) {
            return;
        }
        makeFileExecutable(deviceConsoleBinary);
    }

    @Override
    public boolean isEmulator() {
        return false;
    }
}
