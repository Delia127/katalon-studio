package com.kms.katalon.composer.mobile.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.mobile.constants.ComposerMobileMessageConstants;
import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public class IosDeviceSelectionDialog extends MobileDeviceSelectionDialog {

    private static final String LNK_IOS_DEVICES_TROUBLESHOOT = 
            "https://docs.katalon.com/display/KD/Mobile+on+macOS#MobileonmacOS-Troubleshootcommonissues";

    public IosDeviceSelectionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected List<? extends MobileDeviceInfo> getMobileDevices()
            throws MobileSetupException, IOException, InterruptedException {
        List<IosDeviceInfo> iosDevices = new ArrayList<>();
        iosDevices.addAll(MobileDeviceProvider.getIosDevices());
        iosDevices.addAll(MobileDeviceProvider.getIosSimulators());
        return iosDevices;
    }

    @Override
    protected String getTroubleshootLink() {
        return LNK_IOS_DEVICES_TROUBLESHOOT;
    }

    @Override
    public String getDialogTitle() {
        return ComposerMobileMessageConstants.DIA_TITLE_IOS_DEVICES;
    }
}
