package com.kms.katalon.execution.mobile.device;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.execution.mobile.exception.AndroidSDKNotFoundException;
import com.kms.katalon.execution.mobile.exception.AndroidSetupException;

public class AndroidDeviceInfo extends MobileDeviceInfo {

    private static final String BIN = "bin";

    private static final String UNIX_PATH_SEPARATOR = ":";

    private static final String WIN32_PATH_SEPARATOR = ";";

    private static final String PATH = "PATH";

    private static final String JAVA_HOME = "JAVA_HOME";

    private static final String JRE = "jre";

    private static final String MAC_JRE_HOME_RELATIVE_PATH = JRE + File.separator + "Contents" + File.separator + "Home"
            + File.separator + JRE;

    private static final String EMULATOR_SUFFIX = ")";

    private static final String FOR_ANDROID_VERSION = " - Android ";

    private static final String ANDROID_EMULATOR_PREFIX = "emulator-";

    private static final String ANDROID_HOME_ENVIRONMENT_VARIABLE_NAME = "ANDROID_HOME";

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

    public boolean isEmulator() {
        return isEmulator;
    }

    public AndroidDeviceInfo(String deviceId) throws AndroidSetupException, IOException, InterruptedException {
        super(deviceId);
        isEmulator = deviceId.startsWith(ANDROID_EMULATOR_PREFIX);
        initDeviceProperties();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(deviceId)
                .append(deviceManufacture)
                .append(deviceModel)
                .append(deviceModel)
                .append(deviceOSVersion)
                .append(deviceOs)
                .append(isEmulator)
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
        AndroidDeviceInfo other = (AndroidDeviceInfo) obj;
        return new EqualsBuilder().append(this.deviceId, other.deviceId)
                .append(this.deviceManufacture, other.deviceManufacture)
                .append(this.deviceModel, other.deviceModel)
                .append(this.deviceOSVersion, other.deviceOSVersion)
                .append(this.deviceOs, other.deviceOs)
                .append(this.isEmulator, this.isEmulator)
                .isEquals();
    }

    protected void initDeviceProperties() throws IOException, InterruptedException, AndroidSetupException {
        deviceManufacture = initAndroidDeviceManufacturer();
        deviceModel = initAndroidDeviceModel();
        deviceOs = initAndroidDeviceOS();
        deviceOSVersion = initAndroidDeviceOSVersion();
    }

    private String initAndroidDeviceOS() throws AndroidSetupException, IOException, InterruptedException {
        String[] getOSCommand = new String[] { getADBPath(), S_FLAG, this.deviceId, SHELL, GETPROP_COMMAND,
                NET_BT_NAME };
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

    public static File getAndroidSDKDirectory() throws IOException, AndroidSetupException {
        AndroidSDKManager androidSDKManager = new AndroidSDKManager();
        if (!androidSDKManager.checkSDKExists()) {
            throw new AndroidSDKNotFoundException();
        }
        return androidSDKManager.getSDKFolder();
    }

    public static String getADBPath() throws IOException, AndroidSetupException {
        if (!StringUtils.isEmpty(adbPath)) {
            return adbPath;
        }
        adbPath = new File(getAndroidSDKDirectory(), PLATFORM_TOOLS + File.separator + ADB).getAbsolutePath();
        return adbPath;
    }

    public static void makeAllAndroidSDKBinaryExecutable()
            throws IOException, InterruptedException, AndroidSetupException {
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
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
        ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(
                new String[] { "chmod", "-R", "+x", parentFolder.getAbsolutePath() });
    }

    @Override
    public String getDisplayName() {
        if (isEmulator) {
            return getDeviceId() + " (" + getDeviceModel() + FOR_ANDROID_VERSION + getDeviceOSVersion()
                    + EMULATOR_SUFFIX;
        }
        return getDeviceManufacturer() + " " + getDeviceModel() + " (" + getDeviceOS() + " " + getDeviceOSVersion()
                + ")";
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

    public static Map<String, String> getAndroidAdditionalEnvironmentVariables()
            throws IOException, AndroidSetupException {
        String androidSDKFolder = getAndroidSDKDirectory().getAbsolutePath();
        if (StringUtils.isEmpty(androidSDKFolder)) {
            return new HashMap<String, String>();
        }
        Map<String, String> addtionalEnvironmentVariables = new HashMap<String, String>();
        addtionalEnvironmentVariables.put(ANDROID_HOME_ENVIRONMENT_VARIABLE_NAME, androidSDKFolder);
        File jreFolder = getJREFolder();
        addtionalEnvironmentVariables.put(JAVA_HOME, jreFolder.getAbsolutePath());
        String path = System.getenv(PATH)
                + ((Platform.getOS() == Platform.OS_WIN32) ? WIN32_PATH_SEPARATOR : UNIX_PATH_SEPARATOR)
                + jreFolder + File.separator + BIN;
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            path += UNIX_PATH_SEPARATOR + "/usr/local/bin/";
        }
        addtionalEnvironmentVariables.put(PATH, path);
        return addtionalEnvironmentVariables;
    }

    private static File getJREFolder() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_MOBILE_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(System.getProperty("java.home"));
        }
        // run as product
        File parentFile = getConfigurationFolder().getParentFile();
        if (Platform.OS_MACOSX.equals(Platform.getOS())) {
            return new File(parentFile, MAC_JRE_HOME_RELATIVE_PATH);
        }
        return new File(parentFile, JRE);
    }

    private static File getConfigurationFolder() throws IOException {
        File configurationFolder = new File(
                FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile());
        return configurationFolder;
    }
}
