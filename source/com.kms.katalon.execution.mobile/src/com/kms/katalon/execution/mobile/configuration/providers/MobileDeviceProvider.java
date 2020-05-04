package com.kms.katalon.execution.mobile.configuration.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.execution.mobile.device.AndroidDeviceInfo;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.device.IosSimulatorInfo;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public class MobileDeviceProvider {

    private static final String ANDROID_ADB_DEVICES_COMMAND = "devices";

    private static final String LIST_OF_DEVICES = "list of devices";

    private static final String ANDROID_DEVICE = "device";

    private static final String UNAVAILABLE = "unavailable";

    private static final String SHUTDOWN = "(Shutdown)";

    private static final String BOOTED = "(Booted)";

    private static final String IOS_SIMULATOR_OS_VERSION_STRING_PREFIX = "--";

    private static final String[] GET_SIMULATOR_LIST_COMMAND = new String[] { "/bin/sh", "-c",
            "xcrun simctl list | sed -e '/== Devices ==/,/== Device Pairs ==/!d'" };
    
    private static final Pattern IOS_UUID_PATTERN = Pattern
            .compile("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"); 

    private MobileDeviceProvider() {
    }

    public static List<AndroidDeviceInfo> getAndroidDevices()
            throws MobileSetupException, IOException, InterruptedException {
        AndroidDeviceInfo.makeAllAndroidSDKBinaryExecutable();
        String[] getDevicesCommand = new String[] { AndroidDeviceInfo.getADBPath(), ANDROID_ADB_DEVICES_COMMAND };
        List<String> deviceIds = new ArrayList<String>();
        List<String> deviceResultLines = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(getDevicesCommand);
        for (String resultLine : deviceResultLines) {
            String trimmedLowerCaseResult = resultLine.toLowerCase().trim();
            if (StringUtils.isEmpty(resultLine) || trimmedLowerCaseResult.contains(LIST_OF_DEVICES)) {
                continue;
            }
            if (trimmedLowerCaseResult.contains(ANDROID_DEVICE)) {
                deviceIds.add(resultLine.split("\\s")[0]);
            }
        }

        List<AndroidDeviceInfo> androidDeviceInfos = new ArrayList<AndroidDeviceInfo>();
        for (String deviceId : deviceIds) {
            androidDeviceInfos.add(new AndroidDeviceInfo(deviceId));
        }
        return androidDeviceInfos;
    }

    public static List<IosDeviceInfo> getIosSimulators() throws IOException, InterruptedException {
        if (!isRunningOnMacOSX()) {
            return Collections.emptyList();
        }
        Map<String, String> iosAdditionalEnvironmentVariables = IosDeviceInfo.getIosAdditionalEnvironmentVariables();
        List<String> simulatorsList = ConsoleCommandExecutor
                .runConsoleCommandAndCollectResults(GET_SIMULATOR_LIST_COMMAND, iosAdditionalEnvironmentVariables);
        return getIosSimulatorsFromCommandResult(simulatorsList);
    }

    public static List<IosDeviceInfo> getIosSimulatorsFromCommandResult(List<String> simulatorsList)
            throws IOException, InterruptedException {
        List<IosDeviceInfo> iosDevices = new ArrayList<IosDeviceInfo>();
        String currentOsVersion = null;
        for (String simulatorLine : simulatorsList) {
            if (isIosSimulatorVersionLine(simulatorLine)) {
                currentOsVersion = getCurrentIOSVersion(simulatorLine);
                continue;
            }
            if (!isIosSimulatorInfoLine(simulatorLine)) {
                continue;
            }

            Matcher matcher = IOS_UUID_PATTERN.matcher(simulatorLine);
            if (matcher.find()) {
                String simulatorId = matcher.group();
                String simulatorName = simulatorLine.substring(0, matcher.start() - 1).trim();
                iosDevices.add(new IosSimulatorInfo(simulatorId, simulatorName, currentOsVersion));
            }
        }
        return iosDevices;
    }

    public static List<IosDeviceInfo> getIosDevices() throws IOException, InterruptedException {
        if (!isRunningOnMacOSX()) {
            return Collections.emptyList();
        }
        List<IosDeviceInfo> iosDevices = new ArrayList<>();
        List<String> deviceIds = IosDeviceInfo.executeCommand("idevice_id -l");

        for (String deviceId : deviceIds) {
            iosDevices.add(new IosDeviceInfo(deviceId));
        }
        return iosDevices;
    }

    private static boolean isIosSimulatorInfoLine(String simulatorLine) {
        return (simulatorLine.contains(BOOTED) || simulatorLine.contains(SHUTDOWN))
                && !(simulatorLine.contains(UNAVAILABLE));
    }

    private static boolean isIosSimulatorVersionLine(String simulatorLine) {
        return simulatorLine.startsWith(IOS_SIMULATOR_OS_VERSION_STRING_PREFIX)
                && simulatorLine.endsWith(IOS_SIMULATOR_OS_VERSION_STRING_PREFIX);
    }

    private static String getCurrentIOSVersion(String simulatorLine) {
        int prefixLength = IOS_SIMULATOR_OS_VERSION_STRING_PREFIX.length();
        String versionString = simulatorLine
                .substring(simulatorLine.indexOf(IOS_SIMULATOR_OS_VERSION_STRING_PREFIX) + prefixLength,
                        simulatorLine.lastIndexOf(IOS_SIMULATOR_OS_VERSION_STRING_PREFIX))
                .trim();
        String[] versionStringParts = versionString.split(" ");
        if (versionStringParts.length == 2) {
            return versionStringParts[1];
        }
        return null;
    }

    private static boolean isRunningOnMacOSX() {
        return Platform.getOS().equals(Platform.OS_MACOSX);
    }

    public static MobileDeviceInfo getDevice(MobileDriverType mobileDriverType, String deviceId)
            throws IOException, InterruptedException, MobileSetupException {
        switch (mobileDriverType) {
            case ANDROID_DRIVER:
                return getAndroidDevice(deviceId);
            case IOS_DRIVER:
                return getIosDevice(deviceId);
        }
        return null;
    }

    public static AndroidDeviceInfo getAndroidDevice(String deviceId)
            throws MobileSetupException, IOException, InterruptedException {
        for (AndroidDeviceInfo androidDeviceInfo : getAndroidDevices()) {
            if (StringUtils.equals(androidDeviceInfo.getDeviceId(), deviceId)) {
                return androidDeviceInfo;
            }
        }
        return null;
    }

    public static IosDeviceInfo getIosDevice(String deviceId)
            throws MobileSetupException, IOException, InterruptedException {
        for (IosDeviceInfo iosDeviceInfo : getIosDevices()) {
            if (StringUtils.equals(iosDeviceInfo.getDeviceId(), deviceId)) {
                return iosDeviceInfo;
            }
        }
        for (IosDeviceInfo iosDeviceInfo : getIosSimulators()) {
            if (StringUtils.equals(iosDeviceInfo.getDeviceId(), deviceId)) {
                return iosDeviceInfo;
            }
        }
        return null;
    }
}
