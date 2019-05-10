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
import com.kms.katalon.logging.LogUtil;

public class IosDeviceInfo extends MobileDeviceInfo {
    private static final String RELATIVE_PATH_TO_TOOLS_FOLDER = "resources/tools/";

    private static final String PATH = "PATH";

    private static final String PRODUCT_TYPE_INFO_PREFIX = "ProductType:";

    private static final String PRODUCT_VERSION_INFO_PREFIX = "ProductVersion:";

    private static final String DEVICE_NAME_INFO_PREFIX = "DeviceName:";

    private static final String DEVICE_CLASS_INFO_PREFIX = "DeviceClass:";

    public static final String DEVICECONSOLE = "deviceconsole";

    public static final String DEVICE_CONSOLE_FOLDER_RELATIVE_PATH = RELATIVE_PATH_TO_TOOLS_FOLDER + DEVICECONSOLE;

    protected String deviceClass = "";

    protected String deviceName = "";

    protected String deviceOSVersion = "";

    protected String deviceType = "";

    public IosDeviceInfo(String deviceId) throws IOException, InterruptedException {
        super(deviceId);
        initDeviceInfos(deviceId);
    }

    public static List<String> executeCommand(String command) throws IOException, InterruptedException {
        Map<String, String> env = new HashMap<>();
        env.putAll(getIosAdditionalEnvironmentVariables());
        return ConsoleCommandExecutor.runConsoleCommandAndCollectResults(new String[] { "sh", "-c", command }, env, "");
    }

    protected void initDeviceInfos(String deviceId) throws IOException, InterruptedException {
        List<String> pairedInfos = executeCommand("idevicepair pair -u " + deviceId);
        String pairedString = StringUtils.join(pairedInfos, "\n");
        if (!StringUtils.containsIgnoreCase(pairedString, "SUCCESS")) {
            LogUtil.printErrorLine(pairedString);
        }

        List<String> deviceInfos = executeCommand("ideviceinfo -u " + deviceId);
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
        if (StringUtils.isEmpty(deviceName)) {
            LogUtil.printErrorLine(StringUtils.join(deviceInfos, "\n"));
        }

        executeCommand("idevicepair unpair -u " + deviceId);
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public String getDisplayName() {
        String displayName = deviceClass + " " + deviceName + " " + deviceOSVersion;
        if (StringUtils.isNoneBlank(displayName)) {
            return displayName;
        }
        return "<unrecognized device>";
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
    
    public static File getDeviceConsoleFolder() throws IOException {
        return getResourceFolder(DEVICE_CONSOLE_FOLDER_RELATIVE_PATH);
    }

    public static File getToolsFolder() throws IOException {
        return getResourceFolder(RELATIVE_PATH_TO_TOOLS_FOLDER);
    }

    public static File getDeviceConsoleExecutablePath() throws IOException {
        return new File(getResourceFolder(DEVICE_CONSOLE_FOLDER_RELATIVE_PATH), DEVICECONSOLE);
    }

    @Override
    protected Map<String, String> getEnvironmentVariables() throws IOException, InterruptedException {
        return getIosAdditionalEnvironmentVariables();
    }

    public static Map<String, String> getIosAdditionalEnvironmentVariables() throws IOException, InterruptedException {
        makeDeviceConsoleExecutable();
        Map<String, String> additionalEnvironmentVariables = new HashMap<String, String>();
        additionalEnvironmentVariables.put(PATH, StringUtils.defaultString(System.getenv(PATH)) + ":/usr/local/bin");
        return additionalEnvironmentVariables;
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
