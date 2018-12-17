package com.kms.katalon.execution.mobile.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.execution.mobile.constants.ExecutionMobileMessageConstants;
import com.kms.katalon.execution.mobile.constants.MobilePreferenceConstants;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class MobileExecutionUtil {
    private static final String MAC_DEFAULT_NODEJS_LOCATION = "/usr/local/bin/node";

    public static void detectInstalledAppiumAndNodeJs() throws MobileSetupException {
        String appiumDir = null;
        try {
            appiumDir = findAppiumDir();
        } catch (IOException e) {
            LogUtil.logError(e);
        }

        String nodeEnvPath = StringUtils.EMPTY;
        try {
            nodeEnvPath = detectNodeInstallation();
        } catch (InterruptedException ignore) {
            // ignore this
        } catch (IOException e) {
            LogUtil.logError(e);
        }

        String errorMessage = StringUtils.EMPTY;
        if (StringUtils.isEmpty(appiumDir) && StringUtils.isEmpty(nodeEnvPath)) {
            errorMessage = ExecutionMobileMessageConstants.MSG_NO_APPIUM_AND_NODEJS;
        }
        if (StringUtils.isEmpty(appiumDir)) {
            errorMessage = ExecutionMobileMessageConstants.MSG_NO_APPIUM;
        }
        if (StringUtils.isEmpty(nodeEnvPath)) {
            errorMessage = ExecutionMobileMessageConstants.MSG_NO_NODEJS;
        }
        if (!errorMessage.isEmpty()) {
            throw new MobileSetupException(errorMessage);
        }
    }

    private static String findAppiumDir() throws IOException {
        final ScopedPreferenceStore mobilePreferenceStore = PreferenceStoreManager
                .getPreferenceStore(MobilePreferenceConstants.MOBILE_QUALIFIER);
        String appiumDir = mobilePreferenceStore.getString(MobilePreferenceConstants.MOBILE_APPIUM_DIRECTORY);
        if (StringUtils.isNotEmpty(appiumDir)) {
            return appiumDir;
        }
        appiumDir = findAppiumFromDefaultLocation();
        if (StringUtils.isNotEmpty(appiumDir) && new File(appiumDir).exists()) {
            mobilePreferenceStore.setValue(MobilePreferenceConstants.MOBILE_APPIUM_DIRECTORY, appiumDir);
            mobilePreferenceStore.save();
        }
        return appiumDir;
    }

    private static String findAppiumFromDefaultLocation() {
        switch (Platform.getOS()) {
            case Platform.OS_MACOSX:
                return "/usr/local/lib/node_modules/appium";
            case Platform.OS_LINUX:
            	return "/usr/lib/node_modules/appium/";
            case Platform.OS_WIN32:
                return System.getProperty("user.home") + "\\AppData\\Roaming\\npm\\node_modules\\appium";
            default:
                return null;
        }
    }

    private static String detectNodeInstallation() throws IOException, InterruptedException {
        String cmd = "";
        if (StringUtils.equals(Platform.getOS(), Platform.OS_WIN32)) {
            cmd = "where node";
        } else if (StringUtils.equals(Platform.getOS(), Platform.OS_MACOSX)
                || StringUtils.equals(Platform.getOS(), Platform.OS_LINUX)) {
            // Detect default NODE installation location first
            File nodeJS = new File(MAC_DEFAULT_NODEJS_LOCATION);
            if (nodeJS.exists() && nodeJS.isFile()) {
                return MAC_DEFAULT_NODEJS_LOCATION;
            }
            cmd = "which node";
        }
        Process proc = Runtime.getRuntime().exec(cmd);
        int exitVal = proc.waitFor();
        StringBuilder sb = new StringBuilder();
        if (exitVal == 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
