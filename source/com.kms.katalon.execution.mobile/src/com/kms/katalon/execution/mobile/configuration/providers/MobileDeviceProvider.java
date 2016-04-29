package com.kms.katalon.execution.mobile.configuration.providers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.exception.MobileSetupException;
import com.kms.katalon.execution.mobile.device.AndroidDeviceInfo;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.util.ConsoleCommandExecutor;

public class MobileDeviceProvider {
    private MobileDeviceProvider() {
    }

    public static List<? extends MobileDeviceInfo> getDevices(MobileDriverType mobileDriverType) throws IOException,
            InterruptedException, MobileSetupException {
        switch (mobileDriverType) {
            case ANDROID_DRIVER:
                return getAndroidDevices();
            case IOS_DRIVER:
                return getIosDevices();
        }
        return new ArrayList<>();
    }

    public static List<AndroidDeviceInfo> getAndroidDevices() throws MobileSetupException, IOException,
            InterruptedException {
        String[] getDevicesCommand = new String[] { AndroidDeviceInfo.getADBPath(), "devices" };
        List<String> deviceIds = new ArrayList<String>();
        List<String> deviceResultLines = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(getDevicesCommand);
        for (String resultLine : deviceResultLines) {
            if (StringUtils.isEmpty(resultLine) || resultLine.toLowerCase().trim().contains("list of devices")) {
                continue;
            }
            if (resultLine.toLowerCase().trim().contains("device")) {
                deviceIds.add(resultLine.split("\\s")[0]);
            }
        }

        List<AndroidDeviceInfo> androidDeviceInfos = new ArrayList<AndroidDeviceInfo>();
        for (String deviceId : deviceIds) {
            androidDeviceInfos.add(new AndroidDeviceInfo(deviceId));
        }
        return androidDeviceInfos;
    }

    public static List<IosDeviceInfo> getIosDevices() throws IOException, InterruptedException {
        if (!isRunningOnMacOSX()) {
            return Collections.emptyList();
        }
        IosDeviceInfo.makeAllIMobileDeviceBinaryExecuteAble();
        List<IosDeviceInfo> iosDevices = new ArrayList<IosDeviceInfo>();
        String[] getDeviceIdsCommand = {
                IosDeviceInfo.getIMobileDeviceDirectoryAsString() + File.separator + "idevice_id", "-l" };
        List<String> deviceIds = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(getDeviceIdsCommand,
                IosDeviceInfo.getIosAdditionalEnvironmentVariables());

        for (String deviceId : deviceIds) {
            iosDevices.add(new IosDeviceInfo(deviceId));
        }
        return iosDevices;
    }

    private static boolean isRunningOnMacOSX() {
        return Platform.getOS().equals(Platform.OS_MACOSX);
    }

    public static MobileDeviceInfo getDevice(MobileDriverType mobileDriverType, String deviceId) throws IOException,
            InterruptedException, MobileSetupException {
        switch (mobileDriverType) {
            case ANDROID_DRIVER:
                return getAndroidDevice(deviceId);
            case IOS_DRIVER:
                return getIosDevice(deviceId);
        }
        return null;
    }

    public static AndroidDeviceInfo getAndroidDevice(String deviceId) throws MobileSetupException, IOException,
            InterruptedException {
        for (AndroidDeviceInfo androidDeviceInfo : getAndroidDevices()) {
            if (StringUtils.equals(androidDeviceInfo.getDeviceId(), deviceId)) {
                return androidDeviceInfo;
            }
        }
        return null;
    }

    public static IosDeviceInfo getIosDevice(String deviceId) throws MobileSetupException, IOException,
            InterruptedException {
        for (IosDeviceInfo iosDeviceInfo : getIosDevices()) {
            if (StringUtils.equals(iosDeviceInfo.getDeviceId(), deviceId)) {
                return iosDeviceInfo;
            }
        }
        return null;
    }
}
