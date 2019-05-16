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
        LogUtil.logInfo("INFO: User home: " + getUserHome());
        LogUtil.logInfo("INFO: Java vendor: " + javaVendor);
        LogUtil.logInfo("INFO: Java version: " + javaVersion);
        LogUtil.logInfo("INFO: Local OS: " + getLocalOS());
        LogUtil.logInfo("INFO: CPU load: " + getProcessCpuLoad());
        LogUtil.logInfo("INFO: Total memory: " + getTotalMemoryInMegabyte());
        LogUtil.logInfo("INFO: Free memory: " + getFreeMemoryInMegabyte());
        LogUtil.logInfo("\n");
    }

    private static String maskSensitiveArgs() {
        List<String> markedArgs = new ArrayList<>();
        for (String arg : Platform.getCommandLineArgs()) {
            if (arg.startsWith("-apiKey=")) {
                markedArgs.add("-apiKey=******");
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
            markedArgs.add(arg);
        }
        return StringUtils.join(markedArgs.toArray(new String[0]), " ");
    }
}
