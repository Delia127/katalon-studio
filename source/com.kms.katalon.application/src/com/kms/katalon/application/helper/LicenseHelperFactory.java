package com.kms.katalon.application.helper;

public class LicenseHelperFactory {
    
    private static LicenseHelper licenseHelper;

    public static LicenseHelper get() {
        if (licenseHelper == null) {
            licenseHelper = new LicenseHelper();
        }
        return licenseHelper;
    }
    
    public static void set(LicenseHelper licenseHelperInstance) {
        licenseHelper = licenseHelperInstance;
    }
}
