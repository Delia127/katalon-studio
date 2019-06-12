package com.kms.katalon.composer.execution.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.MessageDialogWithLink;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.CallabelUISynchronize;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.jobs.DownloadSDKProgress;
import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.device.AndroidSDKManager;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;

public class MobileDeviceUIProvider {
    private MobileDeviceUIProvider() {
    }
    
    public static List<MobileDeviceInfo> getAndroidDevices() {
        List<MobileDeviceInfo> mobileDeviceInfos = new ArrayList<MobileDeviceInfo>();
        Shell activeShell = null;
        try {
            boolean sdkExists = CallabelUISynchronize.newInstance()
                    .syncCallabelExec(() -> checkAndroidSDKExist(activeShell));

            if (sdkExists) {
                mobileDeviceInfos.addAll(MobileDeviceProvider.getAndroidDevices());
            }
        } catch (Exception e) {
            UISynchronizeService.syncExec(() -> {
                MessageDialog.openInformation(activeShell, "Error", e.getClass().getName() + ": " + e.getMessage());
            });
        }
        return mobileDeviceInfos;
    }
    
    public static List<MobileDeviceInfo> getIOSDevices() {
        List<MobileDeviceInfo> mobileDeviceInfos = new ArrayList<MobileDeviceInfo>();
        Shell activeShell = null;
        try {
            mobileDeviceInfos.addAll(MobileDeviceProvider.getIosDevices());
        } catch (InterruptedException | IOException e) {
            UISynchronizeService.syncExec(() -> {
                MessageDialog.openInformation(activeShell, "Error", e.getClass().getName() + ": " + e.getMessage());
            });
        }
        try {
            mobileDeviceInfos.addAll(MobileDeviceProvider.getIosSimulators());
        } catch (InterruptedException | IOException e) {
            UISynchronizeService.syncExec(() -> {
                MessageDialog.openInformation(activeShell, "Error", e.getClass().getName() + ": " + e.getMessage());
            });
        }
        return mobileDeviceInfos;
    }

    public static List<MobileDeviceInfo> getAllDevices() {
        List<MobileDeviceInfo> mobileDeviceInfos = new ArrayList<MobileDeviceInfo>();
        Shell activeShell = null;
        try {
            boolean sdkExists = CallabelUISynchronize.newInstance()
                    .syncCallabelExec(() -> checkAndroidSDKExist(activeShell));

            if (sdkExists) {
                mobileDeviceInfos.addAll(MobileDeviceProvider.getAndroidDevices());
            }
        } catch (Exception e) {
            UISynchronizeService.syncExec(() -> {
                MessageDialog.openInformation(activeShell, "Error", e.getClass().getName() + ": " + e.getMessage());
            });
        }
        try {
            mobileDeviceInfos.addAll(MobileDeviceProvider.getIosDevices());
        } catch (InterruptedException | IOException e) {
            UISynchronizeService.syncExec(() -> {
                MessageDialog.openInformation(activeShell, "Error", e.getClass().getName() + ": " + e.getMessage());
            });
        }
        try {
            mobileDeviceInfos.addAll(MobileDeviceProvider.getIosSimulators());
        } catch (InterruptedException | IOException e) {
            UISynchronizeService.syncExec(() -> {
                MessageDialog.openInformation(activeShell, "Error", e.getClass().getName() + ": " + e.getMessage());
            });
        }
        return mobileDeviceInfos;
    }

    public static boolean checkAndroidSDKExist(Shell activeShell) {
        AndroidSDKManager sdkManager = new AndroidSDKManager();
        if (!sdkManager.checkSDKExists()) {
            if (!MessageDialog.openConfirm(activeShell, StringConstants.CONFIRMATION,
                    ComposerExecutionMessageConstants.DIA_MSG_ANDROID_SDK_MISSING)) {
                return false;
            }
            return newDownloadSDKProgress(activeShell, sdkManager);
        }
        return true;
    }

    public static boolean newDownloadSDKProgress(Shell activeShell, AndroidSDKManager sdkManager) {
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(activeShell);
        try {
            dialog.run(true, false, new DownloadSDKProgress(sdkManager.getSDKLocator()));
            return true;
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e);
            MessageDialogWithLink.openError(activeShell, "Error", e.getTargetException().getMessage());
            return false;
        }
    }
}
