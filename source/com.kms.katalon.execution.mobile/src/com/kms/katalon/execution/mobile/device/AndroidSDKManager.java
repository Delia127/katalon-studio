package com.kms.katalon.execution.mobile.device;

import java.io.File;

import com.kms.katalon.constants.GlobalStringConstants;

public class AndroidSDKManager {

    private AndroidSDKLocator sdkLocator;

    public AndroidSDKManager() {
        sdkLocator = new AndroidSDKLocator(new File(GlobalStringConstants.APP_USER_DIR_LOCATION, "tools"));
    }

    public boolean checkSDKExists() {
        return sdkLocator.checkSDKExists();
    }

    public File getSDKFolder() {
        return sdkLocator.getSDKFolder();
    }
    
    public AndroidSDKLocator getSDKLocator() {
        return this.sdkLocator;
    }
}
