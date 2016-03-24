package com.kms.katalon.execution.mobile.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;
import com.kms.katalon.execution.mobile.driver.MobileDevice;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;

public class MobileExecutionUtil {
    private static final String RO_BUILD_VERSION_RELEASE = "ro.build.version.release";

    private static final String RO_PRODUCT_MODEL = "ro.product.model";

    private static final String RO_PRODUCT_NAME = "ro.product.name";

    private static final String ABD_COMMAND_GETPROP_OPTION = "getprop";

    private static final String ADB_COMMAND_SHELL_OPTION = "shell";

    private static final String ADB_COMMAND_S_OPTION = "-s";

    private static final String DEVICE_LINE_SPLITTER = "\\s";

    private static final String DEVICE_LINE_STARTER = "device";

    private static final String LIST_OF_DEVICES_LINE_STARTER = "list of devices";

    private static final String ADB_OPTION_LIST_DEVICES = "devices";

    private static final String ADB_EXECUTATBLE = "adb";

    private static final String ANDROID_PLATFORM_TOOLS_FOLDER = "platform-tools";

    private static final String ANDROID_HOME_ENVIRONMENT_VARIABLE = "ANDROID_HOME";

    private static final String PRODUCT_VERSION_LINE_STARTER = "ProductVersion:";

    private static final String DEVICE_NAME_LINE_STARTER = "DeviceName:";

    private static final String DEVICE_CLASS_LINE_STARTER = "DeviceClass:";

    private static final String IDEVICEINFO_OPTION = "-u";

    private static final String IDEVICEINFO_COMMAND = "ideviceinfo";

    private static final String IDEVICEID_LIST_OPTION = "-l";

    private static final String IDEVICEID_COMMAND = "idevice_id";

    public static MobileDriverConnector getMobileDriverConnector(MobileDriverType mobileDriverType, String projectDirectory)
            throws IOException {
        switch (mobileDriverType) {
            case ANDROID_DRIVER:
                return new AndroidDriverConnector(projectDirectory + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME);
            case IOS_DRIVER:
                return new IosDriverConnector(projectDirectory + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME);
        }
        return null;
    }

    public static Map<String, MobileDevice> getIosDevices() throws IOException, InterruptedException {
        Map<String, MobileDevice> deviceMap = new HashMap<String, MobileDevice>();

        if (!(Platform.getOS().equals(Platform.OS_MACOSX))) {
            return deviceMap;
        }

        List<String> deviceIds = new ArrayList<String>();
        String[] cmd = { IDEVICEID_COMMAND, IDEVICEID_LIST_OPTION };
        ProcessBuilder pb = new ProcessBuilder(cmd);

        Process p = pb.start();
        p.waitFor();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            while ((line = br.readLine()) != null) {
                deviceIds.add(line);
            }
        } finally {
            br.close();
        }

        for (String deviceId : deviceIds) {
            cmd = new String[] { IDEVICEINFO_COMMAND, IDEVICEINFO_OPTION, deviceId };
            pb.command(cmd);
            p = pb.start();
            p.waitFor();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            MobileDevice iosDevice = new MobileDevice(deviceId);
            
            while ((line = br.readLine()) != null) {
                if (line.contains(DEVICE_CLASS_LINE_STARTER)) {
                    iosDevice.setModel(line.substring(DEVICE_CLASS_LINE_STARTER.length(), line.length()).trim());
                    continue;
                }
                if (line.contains(DEVICE_NAME_LINE_STARTER)) {
                    iosDevice.setName(line.substring(DEVICE_NAME_LINE_STARTER.length(), line.length()).trim());
                    continue;
                }
                if (line.contains(PRODUCT_VERSION_LINE_STARTER)) {
                    iosDevice.setVersion(line.substring(PRODUCT_VERSION_LINE_STARTER.length(), line.length()).trim());
                    continue;
                }
            }

            deviceMap.put(deviceId, iosDevice);
        }
        return deviceMap;
    }

    public static Map<String, MobileDevice> getAndroidDevices() throws IOException, InterruptedException {
        Map<String, MobileDevice> deviceMap = new HashMap<String, MobileDevice>();

        String adbPath = System.getenv(ANDROID_HOME_ENVIRONMENT_VARIABLE);
        if (adbPath != null) {
            List<String> deviceIds = new ArrayList<String>();
            adbPath += File.separator + ANDROID_PLATFORM_TOOLS_FOLDER + File.separator + ADB_EXECUTATBLE;
            String[] cmd = new String[] { adbPath, ADB_OPTION_LIST_DEVICES };
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, deviceId;
            try {
                while ((line = br.readLine()) != null) {
                    if (!line.toLowerCase().trim().contains(LIST_OF_DEVICES_LINE_STARTER)) {
                        if (line.toLowerCase().trim().contains(DEVICE_LINE_STARTER)) {
                            deviceId = line.split(DEVICE_LINE_SPLITTER)[0];
                            deviceIds.add(deviceId);
                        }
                    }
                }
                br.close();
            } finally {
                br.close();
            }

            for (String id : deviceIds) {
                try {
                    MobileDevice androidDevice = new MobileDevice(id);
                    cmd = new String[] { adbPath, ADB_COMMAND_S_OPTION, id, ADB_COMMAND_SHELL_OPTION,
                            ABD_COMMAND_GETPROP_OPTION, RO_PRODUCT_NAME };
                    pb.command(cmd);
                    process = pb.start();
                    process.waitFor();

                    br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    androidDevice.setName(br.readLine());
                    br.close();
                    
                    cmd = new String[] { adbPath, ADB_COMMAND_S_OPTION, id, ADB_COMMAND_SHELL_OPTION,
                            ABD_COMMAND_GETPROP_OPTION, RO_PRODUCT_MODEL };
                    pb.command(cmd);
                    process = pb.start();
                    process.waitFor();
                    br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    androidDevice.setModel(br.readLine());                    
                    br.close();

                    cmd = new String[] { adbPath, ADB_COMMAND_S_OPTION, id, ADB_COMMAND_SHELL_OPTION,
                            ABD_COMMAND_GETPROP_OPTION, RO_BUILD_VERSION_RELEASE };
                    pb.command(cmd);
                    process = pb.start();
                    process.waitFor();
                    br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    androidDevice.setVersion(br.readLine());
                    br.close();

                    deviceMap.put(id, androidDevice);
                } finally {
                    if (br == null) {
                        continue;
                    }
                    br.close();
                }
            }
        }

        return deviceMap;
    }

    public static String getDefaultDeviceId(String projectDir, MobileDriverType platform) throws IOException {
        String deviceId = null;
        switch (platform) {
            case ANDROID_DRIVER: {
                deviceId = new AndroidDriverConnector(projectDir+ File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME).getDeviceId();
                break;
            }
            case IOS_DRIVER: {
                deviceId =  new IosDriverConnector(projectDir + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME).getDeviceId();
                break;
            }
        }
        return deviceId;
    }

    public static MobileDevice getDevice(MobileDriverType mobileDriverType, String deviceId) throws IOException,
            InterruptedException {
        Map<String, MobileDevice> deviceMap = null;
        switch (mobileDriverType) {
            case ANDROID_DRIVER:
                deviceMap = MobileExecutionUtil.getAndroidDevices();
                break;
            case IOS_DRIVER:
                deviceMap = MobileExecutionUtil.getIosDevices();
                break;
        }
        return deviceMap.get(deviceId);
    }
}
