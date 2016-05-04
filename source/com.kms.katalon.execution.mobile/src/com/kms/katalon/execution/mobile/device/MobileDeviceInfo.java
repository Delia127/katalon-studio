package com.kms.katalon.execution.mobile.device;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.mobile.util.ConsoleCommandExecutor;

public abstract class MobileDeviceInfo {
    protected static final String X_FLAG = "+x";

    protected static final String CHMOD_COMMAND = "chmod";

    protected static final String OS_RESOURCES_FOLDER = "os_resources";

    protected static final String CONFIGURATION_FOLDER_NAME = "configuration";

    protected String deviceId;

    public MobileDeviceInfo(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public abstract String getDeviceName();

    public abstract String getDeviceManufacturer();

    public abstract String getDeviceModel();

    public abstract String getDeviceOS();

    public abstract String getDeviceOSVersion();

    protected static File getResourceFolder(String resourceFolderRelativePath) throws IOException {
        File bundleFile = FileLocator.getBundleFile(Platform.getBundle(IdConstants.KATALON_MOBILE_BUNDLE_ID));
        if (bundleFile.isDirectory()) {
            // run by IDE
            return getResourcesFolderFromBundle(resourceFolderRelativePath, bundleFile);
        }
        // run as product
        return getResourcesFolderFromBuildResourcesFolder(resourceFolderRelativePath);
    }

    protected static File getResourcesFolderFromBuildResourcesFolder(String resourceFolderRelativePath)
            throws IOException {
        return new File(ClassPathResolver.getConfigurationFolder().getAbsolutePath() + File.separator
                + resourceFolderRelativePath);
    }

    protected static File getResourcesFolderFromBundle(String resourceFolderRelativePath, File bundleFile) {
        return new File(bundleFile + File.separator + OS_RESOURCES_FOLDER + File.separator
                + getResourcesParentFolderFromOS() + File.separator + resourceFolderRelativePath);
    }

    private static String getResourcesParentFolderFromOS() {
        switch (Platform.getOS()) {
            case Platform.OS_WIN32:
                return "win";
            case Platform.OS_MACOSX:
                return "macosx";
            case Platform.OS_LINUX:
                return "linux";
        }
        return "";
    }

    protected static void makeFileExecutable(File file) throws IOException, InterruptedException {
        ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(new String[] { CHMOD_COMMAND, X_FLAG,
                file.getAbsolutePath() });
    }
}
