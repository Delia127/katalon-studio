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

        System.out.println("\n");
        System.out.println(StringUtils.repeat("*", 80));
        System.out.println("Katalon Version: " + getKatalonVersion());
        System.out.println("Command-line arguments: " + maskSensitiveArgs());
        System.out.println("Java vendor: " + javaVendor);
        System.out.println("Java version: " + javaVersion);
        System.out.println("Local OS: " + getLocalOS());
        System.out.println("CPU load: " + getProcessCpuLoad());
        System.out.println("Total memory: " + getTotalMemoryInMegabyte());
        System.out.println("Free memory: " + getFreeMemoryInMegabyte());
        System.out.println("APPIUM_HOME: " + getSystemEnvironment("APPIUM_HOME", "<not set>"));
        System.out.println(StringUtils.repeat("*", 80));
        System.out.println("\n");

        if (Platform.OS_LINUX.equals(Platform.getOS()) && (!isJava8(javaVersion) || !isOpenJDKJRE(javaVendor))) {
            LogUtil.logErrorMessage(
                    "Error! Katalon Studio requires Open JDK/JRE 1.8 to run on Linux, but your Java version is not compatible. Please install and try again.");
        }
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

    private static boolean isJava8(String javaVersion) {
        return javaVersion.startsWith("1.8.");
    }

    private static boolean isOpenJDKJRE(String javaVendor) {
        return StringUtils.containsIgnoreCase(javaVendor, "Open JDK")
                || StringUtils.containsIgnoreCase(javaVendor, "Open JRE");
    }
}
