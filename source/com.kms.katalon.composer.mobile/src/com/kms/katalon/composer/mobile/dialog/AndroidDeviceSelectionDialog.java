package com.kms.katalon.composer.mobile.dialog;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.mobile.constants.ComposerMobileMessageConstants;
import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public class AndroidDeviceSelectionDialog extends MobileDeviceSelectionDialog {
    private static final String LNK_ANDROID_TROUBLESHOOT = 
            "https://docs.katalon.com/display/KD/Mobile+on+Windows#MobileonWindows-Troubleshooting";

    public AndroidDeviceSelectionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected List<? extends MobileDeviceInfo> getMobileDevices()
            throws MobileSetupException, IOException, InterruptedException {
        return MobileDeviceProvider.getAndroidDevices();
    }

    @Override
    protected String getTroubleshootLink() {
        return LNK_ANDROID_TROUBLESHOOT;
    }

    @Override
    public String getDialogTitle() {
        return ComposerMobileMessageConstants.DIA_TITLE_ANDROID_DEVICES;
    }
    
}
