package com.kms.katalon.execution.util;

import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.SystemInformationUtil;

public class LocalInformationUtil {
    private static final int MEGABYTE = 1024 * 1024;

    private static final String BIT = "bit";

    private static final String OS_ARCHITECTURE_PROPERTY = "sun.arch.data.model";

    private static final String OS_NAME_PROPERTY = "os.name";

    public static String getLocalOS() {
        return System.getProperty(OS_NAME_PROPERTY) + " " + System.getProperty(OS_ARCHITECTURE_PROPERTY) + BIT;
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    public static String getKatalonVersion() {
        return ApplicationInfo.versionNo();
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }

    public static String getErrorLog() {
        return Platform.getLogFileLocation().toString();
    }

    public static String getTotalMemoryInMegabyte() {
        try {
            long totalMem = SystemInformationUtil.totalPhysicalMemorySizeInByte();
            return String.format("%.0f MB", (float) (totalMem / MEGABYTE));
        } catch (InstanceNotFoundException | AttributeNotFoundException | MalformedObjectNameException
                | ReflectionException | MBeanException e) {
            return "unknown";
        }
    }

    public static String getFreeMemoryInMegabyte() {
        try {
            long freeMem = SystemInformationUtil.freePhysicalMemorySizeInByte();
            return String.format("%.0f MB", (float) (freeMem / MEGABYTE));
        } catch (InstanceNotFoundException | AttributeNotFoundException | MalformedObjectNameException
                | ReflectionException | MBeanException e) {
            return "unknown";
        }
    }

    public static String getSystemEnvironment(String key, String defaultString) {
        return System.getenv().getOrDefault(key, defaultString);
    }

    public static String getProcessCpuLoad() {
        try {
            double cpuLoad = SystemInformationUtil.getProcessCpuLoad();
            return String.format("%.0f%%", cpuLoad);
        } catch (MalformedObjectNameException | InstanceNotFoundException | ReflectionException e) {
            return "unknown";
        }
    }

    public static void printSystemInformation() {
        String javaVendor = getJavaVendor();
        String javaVersion = getJavaVersion();

        LogUtil.logInfo("\n");
        LogUtil.logInfo("INFO: Katalon Version: " + getKatalonVersion());
        LogUtil.logInfo("INFO: Command-line arguments: " + maskSensitiveArgs());
        LogUtil.logInfo("INFO: User working dir: " + getUserDir());
        LogUtil.logInfo("INFO: Error log: " + getErrorLog());
        LogUtil.logInfo("INFO: Katalon TestOps server URL: " + ApplicationInfo.getTestOpsServer());
        LogUtil.logInfo("INFO: Katalon Store server URL: " + getKatalonStoreUrl());
        LogUtil.logInfo("INFO: User home: " + getUserHome());
        LogUtil.logInfo("INFO: Java vendor: " + javaVendor);
        LogUtil.logInfo("INFO: Java version: " + javaVersion);
        LogUtil.logInfo("INFO: Local OS: " + getLocalOS());
        LogUtil.logInfo("INFO: CPU load: " + getProcessCpuLoad());
        LogUtil.logInfo("INFO: Total memory: " + getTotalMemoryInMegabyte());
        LogUtil.logInfo("INFO: Free memory: " + getFreeMemoryInMegabyte());
        LogUtil.logInfo("INFO: Machine ID: " + MachineUtil.getMachineId());
        LogUtil.logInfo("\n");
    }

    public static void printLicenseServerInfo(String serverURL, String apiKey) {
        LogUtil.logInfo("\n");
        LogUtil.logInfo("INFO: Katalon TestOps server URL: " + serverURL);

        int length = apiKey.length();
        if (length > 7) {
            LogUtil.logInfo(
                    "INFO: API key: " + apiKey.substring(0, 3) + "*****" + apiKey.substring(length - 3, length));
        } else {
            LogUtil.logError("Make sure your API key is valid.");
        }
        LogUtil.logInfo("\n");
    }
    
    private static String maskSensitiveArgs() {
        List<String> markedArgs = new ArrayList<>();
        for (String arg : Platform.getCommandLineArgs()) {
            if (arg.startsWith("-apiKey=")) {
                markedArgs.add("-apiKey=******");
                continue;
            }
            if (arg.startsWith("-apikey=")) {
                markedArgs.add("-apikey=******");
                continue;
            }
            if (arg.startsWith("-username=")) {
                markedArgs.add("-username=******");
                continue;
            }
            if (arg.startsWith("-password=")) {
                markedArgs.add("-password=******");
                continue;
            }
            if (arg.startsWith("-apiKeyOP=")) {
                markedArgs.add("-apiKeyOP=******");
                continue;
            }
            if (arg.startsWith("-apiKeyOnPremise=")) {
                markedArgs.add("-apiKeyOnPremise=******");
                continue;
            }
            markedArgs.add(arg);
        }
        return StringUtils.join(markedArgs.toArray(new String[0]), " ");
    }
    
    
    /**
     * TODO: Duplicated from KStoreUrls. These code will be removed in KS v7.1.0 
     */
    private static final String STORE_URL_PROPERTY_KEY = "storeUrl";

    private static final String DEVELOPMENT_URL = "https://store-staging.katalon.com";

    private static final String PRODUCTION_URL = "https://store.katalon.com";

    public static String getKatalonStoreUrl() {
        String storeUrlArgument = getStoreUrlArgument();
        if (!StringUtils.isBlank(storeUrlArgument)) {
            return storeUrlArgument;
        } else if (VersionUtil.isStagingBuild() || VersionUtil.isDevelopmentBuild()) {
            return DEVELOPMENT_URL;
        } else {
            return PRODUCTION_URL;
        }
    }

    private static String getStoreUrlArgument() {
        return System.getProperty(STORE_URL_PROPERTY_KEY);
    }
}
