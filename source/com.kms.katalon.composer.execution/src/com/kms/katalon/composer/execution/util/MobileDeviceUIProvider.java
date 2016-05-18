package com.kms.katalon.composer.execution.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public class MobileDeviceUIProvider {
    private MobileDeviceUIProvider() {
    }

    public static List<MobileDeviceInfo> getAllDevices() {
        List<MobileDeviceInfo> mobileDeviceInfos = new ArrayList<MobileDeviceInfo>();
        try {
            mobileDeviceInfos.addAll(MobileDeviceProvider.getAndroidDevices());
        } catch (InterruptedException | MobileSetupException | IOException e) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Error", e.getClass().getName() + ": "
                    + e.getMessage());
        }
        try {
            mobileDeviceInfos.addAll(MobileDeviceProvider.getIosDevices());
        } catch (InterruptedException | IOException e) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Error", e.getClass().getName() + ": "
                    + e.getMessage());
        }
        return mobileDeviceInfos;
    }
}
